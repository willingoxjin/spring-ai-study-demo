docker run -d \
--name redis-stack \
-p 9379:6379 \
-v /opt/docker/redis-stack/data:/data \
-v /opt/docker/redis-stack/conf/redis-stack.conf:/etc/redis-stack.conf \
--restart unless-stopped \
redis/redis-stack-server:7.4.0-v8