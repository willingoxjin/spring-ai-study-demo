// 从 Vue 全局对象中解构所需 API
const { createApp, ref, watch, nextTick, onMounted, onBeforeUnmount } = Vue;

// 初始化 Markdown 渲染器
const initMarkdown = () => {
  // 配置 markdown-it
  const md = window.markdownit({
    html: false,        // 禁止 HTML 标签，防止 XSS
    xhtmlOut: true,
    breaks: true,       // 转换换行符为 <br>
    linkify: true,      // 自动识别 URL
    typographer: true,  // 智能引号等
    highlight: function (str, lang) {
      if (lang && hljs.getLanguage(lang)) {
        try {
          return '<pre class="hljs"><code>' +
              hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
              '</code></pre>';
        } catch (__) {}
      }
      return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>';
    }
  });

  // 为表格添加包装器（支持横向滚动）
  md.renderer.rules.table_open = function() {
    return '<div class="table-wrapper">\n<table>\n';
  };
  md.renderer.rules.table_close = function() {
    return '</table>\n</div>';
  };

  // 返回渲染函数
  return (content) => {
    if (!content) return '';
    return md.render(content);
  };
};

// 创建 Vue 应用
createApp({
  setup() {
    // 响应式数据
    const messages = ref([]);
    const inputMessage = ref('');
    const isThinking = ref(false);
    const webEnabled = ref(false);
    const kbEnabled = ref(false);
    const chatMessagesRef = ref(null);
    const textareaRef = ref(null);

    watch(webEnabled, (newVal, oldVal) => {
      if (newVal !== oldVal && newVal) {
        kbEnabled.value = false;
      }
    })
    watch(kbEnabled, (newVal, oldVal) => {
      if (newVal !== oldVal && newVal) {
        webEnabled.value = false;
      }
    })

    // 当前活动的 EventSource
    let currentEventSource = null;

    // Markdown 渲染函数
    const formatMessage = initMarkdown();

    // 滚动到底部
    const scrollToBottom = async () => {
      await nextTick();
      if (chatMessagesRef.value) {
        chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight;
      }
    };

    // 添加消息
    const addMessage = (role, content) => {
      messages.value.push({ role, content });
      scrollToBottom();
    };

    // 更新最后一条 bot 消息（流式追加）
    const updateLastBotMessage = (newContent) => {
      const lastMsg = messages.value[messages.value.length - 1];
      if (lastMsg && lastMsg.role === 'bot') {
        lastMsg.content = newContent;
      } else {
        messages.value.push({ role: 'bot', content: newContent });
      }
      scrollToBottom();
    };

    // 自动调整输入框高度
    const adjustTextareaHeight = () => {
      if (!textareaRef.value) return;
      const el = textareaRef.value;
      el.style.height = 'auto';
      el.style.height = Math.min(el.scrollHeight, 120) + 'px';
    };

    // 清空对话
    const clearChat = () => {
      if (currentEventSource) {
        currentEventSource.close();
        currentEventSource = null;
      }
      messages.value = [{
        role: 'bot',
        content: `对话已清空，随时向我提问！<br>当前联网搜索${webEnabled.value ? '已开启' : '未开启'}，知识库搜索${kbEnabled.value ? '已开启' : '未开启'}。`
      }];
      scrollToBottom();
    };

    const resolveContent = (text) => {
      let data = JSON.parse(text)
      return data.content;
    };

    // 发送消息并建立 SSE 连接
    const sendMessage = async () => {
      const text = inputMessage.value.trim();
      if (!text) return;

      inputMessage.value = '';
      await nextTick();
      adjustTextareaHeight();

      addMessage('user', text);

      if (currentEventSource) {
        currentEventSource.close();
        currentEventSource = null;
      }

      isThinking.value = true;

      // 添加临时占位消息
      const tempBotIndex = messages.value.length;
      messages.value.push({ role: 'bot', content: '⏳ 正在连接后端...' });
      scrollToBottom();

      // 构建 SSE URL
      const clientId = 1;
      const encodedMessage = encodeURIComponent(text);
      const webParam = webEnabled.value ? 'true' : 'false';
      const kbParam = kbEnabled.value ? 'true' : 'false';
      let reqUrl = 'doChat';
      if (kbEnabled.value) {
        reqUrl = 'doChatByRag';
      }
      if (webEnabled.value) {
        reqUrl = 'doChatByWebSearch';
      }

      const url = `http://127.0.0.1:19000/mcp-client/chat/stream/${reqUrl}?clientId=${clientId}&prompt=${encodedMessage}&webSearch=${webParam}&knowledgeBase=${kbParam}`;

      const eventSource = new EventSource(url);
      currentEventSource = eventSource;

      let fullAnswer = '';
      let isFirstChunk = true;

      eventSource.onopen = () => {
        console.log('SSE 连接打开')
      }

      eventSource.addEventListener('chunk', (event) => {
        const chunk = resolveContent(event.data);
        console.log('SSE chunk: ', chunk)
        if (!chunk) return;

        if (isFirstChunk) {
          // 移除占位消息
          if (messages.value[tempBotIndex] && messages.value[tempBotIndex].content === '⏳ 正在连接后端...') {
            messages.value.splice(tempBotIndex, 1);
          }
          fullAnswer = '';
          isFirstChunk = false;
        }

        fullAnswer += chunk;
        updateLastBotMessage(fullAnswer);
        isThinking.value = false;
      });

      eventSource.onerror = (error) => {
        console.error('SSE 连接错误:', error);
        if (isFirstChunk && messages.value[tempBotIndex] && messages.value[tempBotIndex].content === '⏳ 正在连接后端...') {
          messages.value.splice(tempBotIndex, 1);
        }
        const errorMsg = '❌ 连接后端失败，请确保服务已启动且地址正确。';
        if (fullAnswer === '' && !isFirstChunk) {
          updateLastBotMessage(fullAnswer + errorMsg);
        } else if (fullAnswer === '') {
          addMessage('bot', errorMsg);
        } else {
          updateLastBotMessage(fullAnswer + '\n\n' + errorMsg);
        }
        isThinking.value = false;
        if (currentEventSource) {
          currentEventSource.close();
          currentEventSource = null;
        }
      };

      eventSource.addEventListener('close', () => {
        console.log('SSE 连接关闭')
        if (currentEventSource === eventSource) {
          if (currentEventSource) {
            currentEventSource.close();
          }
          currentEventSource = null;
        }
        isThinking.value = false;
      });
    };

    // 键盘事件
    const onKeydown = (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
      }
    };

    const handleInput = () => {
      adjustTextareaHeight();
    };

    // 生命周期
    onMounted(() => {
      adjustTextareaHeight();
      if (textareaRef.value) {
        textareaRef.value.addEventListener('input', handleInput);
      }
      // 添加欢迎消息
      if (messages.value.length === 0) {
        messages.value.push({
          role: 'bot',
          content: '你好！我是你的 AI 助手球球。'
        });
      }
    });

    onBeforeUnmount(() => {
      if (textareaRef.value) {
        textareaRef.value.removeEventListener('input', handleInput);
      }
      if (currentEventSource) {
        currentEventSource.close();
        currentEventSource = null;
      }
    });

    return {
      messages,
      inputMessage,
      isThinking,
      webEnabled,
      kbEnabled,
      chatMessagesRef,
      textareaRef,
      formatMessage,
      clearChat,
      sendMessage,
      onKeydown
    };
  }
}).mount('#app');