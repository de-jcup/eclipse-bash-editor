if [ -f /etc/lsb-release -o -d /etc/lsb-release.d ]; then
      DIST=$(grep "DISTRIB_ID" /etc/lsb-release|awk -F "=" '{print $2}'|tr -d "\"', \n")
      if [ -z "$DIST" ]; then
          DIST="Ubuntu"
      fi
fi