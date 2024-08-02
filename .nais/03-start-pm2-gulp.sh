#!/usr/bin/env bash
set -e


export PM2_HOME="/tmp/.pm2"

pm2 start google-chrome \
  --interpreter none \
  -- \
  --headless \
  --disable-translate \
  --disable-background-networking \
  --safebrowsing-disable-auto-update \
  --disable-sync \
  --metrics-recording-only \
  --disable-default-apps \
  --no-first-run \
  --mute-audio \
  --hide-scrollbars \
  --remote-debugging-port=9222 \
  --no-sandbox
#  --disable-gpu \
#  --disable-extensions \

# remove root and sandbox-setting
echo "Copying"
ls -l /app/pdf.js
cp -r /app/pdf.js /tmp/pdf.js

echo "Starting gulp"
node --version
npm --version
npx --version
cd /tmp/pdf.js
# npx gulp server
# tail -f /dev/null
