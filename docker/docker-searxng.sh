docker run -d \
--name searxng \
-p 7788:8080 \
-v "sear-xng/:/etc/searxng" \
-e "SEARXNG_BASE_URL=http://localhost:$PORT" \
-e "INSTANCE_NAME=instance" \
--restart unless-stopped \
searxng/searxng:2026.4.1-bab3879cb