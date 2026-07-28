#!/usr/bin/env bash
set -euo pipefail

SDK_ROOT="/opt/android-sdk"
CMDLINE_TOOLS_VERSION="11076708"   # commandline-tools version bundled with Android Studio 2024.x
CMDLINE_TOOLS_URL="https://dl.google.com/android/repo/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"

echo "==> Installing Android SDK to ${SDK_ROOT}"
sudo mkdir -p "${SDK_ROOT}"
sudo chown -R "$(whoami)" "${SDK_ROOT}"

if [ ! -d "${SDK_ROOT}/cmdline-tools/latest" ]; then
  echo "==> Downloading Android commandline-tools"
    TMP_ZIP="$(mktemp)"
      curl -fsSL "${CMDLINE_TOOLS_URL}" -o "${TMP_ZIP}"
        mkdir -p "${SDK_ROOT}/cmdline-tools"
          unzip -q "${TMP_ZIP}" -d "${SDK_ROOT}/cmdline-tools"
            # Google's zip extracts to "cmdline-tools" - move contents into "latest" as required layout
              mv "${SDK_ROOT}/cmdline-tools/cmdline-tools" "${SDK_ROOT}/cmdline-tools/latest"
                rm -f "${TMP_ZIP}"
                fi

                export ANDROID_HOME="${SDK_ROOT}"
                export ANDROID_SDK_ROOT="${SDK_ROOT}"
                export PATH="${PATH}:${SDK_ROOT}/cmdline-tools/latest/bin:${SDK_ROOT}/platform-tools"

                echo "==> Accepting SDK licenses"
                yes | sdkmanager --licenses > /dev/null || true

                echo "==> Installing platform-tools, platform 34, build-tools 34.0.0"
                sdkmanager --install \
                  "platform-tools" \
                    "platforms;android-34" \
                      "build-tools;34.0.0" > /dev/null

                      echo "==> Writing local.properties"
                      cat > "$(dirname "$0")/../local.properties" <<EOF
                      sdk.dir=${SDK_ROOT}
                      EOF

                      echo "==> Making gradlew executable"
                      chmod +x "$(dirname "$0")/../gradlew"

                      echo "==> Done. Try: ./gradlew :app:assembleDebug"