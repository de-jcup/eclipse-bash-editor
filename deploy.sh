#!/bin/bash
# SPDX-License-Identifier: Apache 2.0

# Version 1.0
# -----------
# for origin file refer to https://github.com/de-jcup/p2-updatesite-github-pages-automation

#
# How it works
# - JAR signing environment variables must be set
# - jar signing is done automatically
# - we use github pages to provide static content for eclipse marketplace
# - corresponding github repository is automatically calculated based on this repository
#   e.g. "eclipse-yaml-editor" will need a "update-site-eclipse-yaml-editor" repository
# - so by naming conventions this script can be used every where (in de-jcup github context.
#   if you want to use it with another user just set GITHUB_USER with other username
# - eclipse update site project must have "update" inside their project folder names (and we only accept ONE...)
# - The repository must exist on Github
# - The update site must be build before deployment

RED='\033[0;31m'
LIGHT_RED='\033[1;31m'
LIGHT_GREEN='\033[1;32m'
BROWN='\033[0;33m'
NC='\033[0m' # No Color

function headline(){
   echo -e "${BROWN}$1${NC}" 
}

function info(){
   echo -e "- ${LIGHT_GREEN}$1${NC}" 
}


function initVariables(){
    # check preconditions
    if [ "$KEYSTORE_PWD" == "" ]; then
        echo "KEYSTORE_PWD not set!"
        exit 1
    fi
    
    if [ "$KEYSTORE_LOCATION" == "" ]; then
        echo "KEYSTORE_LOCATION not set!"
        exit 1
    fi


    GITHUB_USER="de-jcup"
    SOURCE_PROJECT_DIR=${PWD}
    UPDATE_SITE_NAME=$(file *update* | grep directory | cut -d':' -f1)
    SOURCE_PROJECT_NAME=${SOURCE_PROJECT_DIR##*/}          # we use current directory name to identify (assume the project has been checked out with name like on github
    TARGET_PROJECT_NAME="update-site-${SOURCE_PROJECT_NAME}"
    
    cd ..
    THIS_ROOT_DIR="${PWD}"
    TARGET_PROJECT_DIR="${THIS_ROOT_DIR}/${TARGET_PROJECT_NAME}"
    TARGET_UPDATESITE_DIR="${THIS_ROOT_DIR}/${TARGET_PROJECT_NAME}/update-site"
}

function showHeader(){
    headline "*******************************************************************"
    headline "* Deployment of update site: '${UPDATE_SITE_NAME}'"
    headline "*******************************************************************"
    headline "ROOT  : $THIS_ROOT_DIR"
    headline "TARGET: $TARGET_PROJECT_DIR"
    headline "        $TARGET_UPDATESITE_DIR"
    headline "SOURCE: $SOURCE_PROJECT_DIR"
}



function signNewJarsInUpdateSiteProject(){
    info "start signing jars"
    cd $TARGET_UPDATESITE_DIR
    
    PLUGINDIR=./plugins/*
    FEATUREDIR=./features/*
    
    echo "Processing features dir $FEATUREDIR file..."
    for f in $FEATUREDIR;
    do
      if [ ${f: -7} == ".sha256" ] ; then
        continue
      fi
      CHECKSUM=$(sha256sum $f)
      CHECKSUM_FILENAME="${f}.sha256"
      if [ ! -f "$CHECKSUM_FILENAME" ] ; then
          echo "Signing feature: $f file..."
          jarsigner -keystore $KEYSTORE_LOCATION -storepass:env KEYSTORE_PWD $f signFiles
          echo "$CHECKSUM" > $CHECKSUM_FILENAME
          echo
      else
        echo "> ignore: $f because already signed"   
      fi
    done
    
    echo "Processing plugin dir $PLUGINDIR file..."
    
    for f in $PLUGINDIR;
    do
      if [ ${f: -7} == ".sha256" ]; then
        continue
      fi
      CHECKSUM=$(sha256sum $f)
      CHECKSUM_FILENAME="${f}.sha256"
      if [ ! -f "$CHECKSUM_FILENAME" ]; then
          echo "Signing plugin: $f file..."
          jarsigner -keystore $KEYSTORE_LOCATION -storepass:env KEYSTORE_PWD $f signFiles
          echo "$CHECKSUM" > $CHECKSUM_FILENAME
          echo  
      else
        echo "> ignore: $f because already signed"   
      fi
       # take action on each file. $f store current file name
      echo ""
    done
    
    #
    # Jar signing done...
    #
    info "JAR signing done"
            
}

function ensureCleanUpdateSiteRepository(){
    cd $THIS_ROOT_DIR
    if [ -d "$TARGET_PROJECT_NAME" ]; then
        # Control will enter here if $DIRECTORY doesn't exist.
       info "drop old update-site repository"
       rm $TARGET_PROJECT_DIR -rf
       info "pwd:${PWD}"
    fi
    info "fetch update-site repository from scratch"
    git clone https://github.com/$GITHUB_USER/$TARGET_PROJECT_NAME
    cd $TARGET_PROJECT_DIR
    git pull
}

function copyOriginUpdateSiteContent(){
    if [ ! -d "$TARGET_UPDATESITE_DIR" ] ; then
        info "$TARGET_UPDATESITE_DIR not found, so start creation"
        mkdir $TARGET_UPDATESITE_DIR
    fi
    info "copy from $SOURCE_PROJECT_DIR/$UPDATE_SITE_NAME/*.* to $TARGET_UPDATESITE_DIR"
    cp $SOURCE_PROJECT_DIR/$UPDATE_SITE_NAME/site.xml $TARGET_UPDATESITE_DIR -f
    cp $SOURCE_PROJECT_DIR/$UPDATE_SITE_NAME/artifacts.jar $TARGET_UPDATESITE_DIR -f
    cp $SOURCE_PROJECT_DIR/$UPDATE_SITE_NAME/content.jar $TARGET_UPDATESITE_DIR -f
    
    if [ ! -d "$TARGET_UPDATESITE_DIR/features" ]; then
        # Control will enter here if $DIRECTORY doesn't exist.
        mkdir $TARGET_UPDATESITE_DIR/features
    fi
    cp $SOURCE_PROJECT_DIR/$UPDATE_SITE_NAME/features/*.jar $TARGET_UPDATESITE_DIR/features -rn
    
    if [ ! -d "$TARGET_UPDATESITE_DIR/plugins" ]; then
        # Control will enter here if $DIRECTORY doesn't exist.
        mkdir $TARGET_UPDATESITE_DIR/plugins
    fi
    cp $SOURCE_PROJECT_DIR/$UPDATE_SITE_NAME/plugins/*.jar $TARGET_UPDATESITE_DIR/plugins -rn
   
}

function addFilesToUpdateSiteRepo(){
    cd $TARGET_PROJECT_DIR
    info "add all new stuff to git repository"
    git add --all
}
function commitAndPushUpdateSiteRepo(){
    cd $TARGET_PROJECT_DIR
    info "commit"s
    git commit -m"released new changes to p2 plugin updatesite"
    
    info "push"
    git push
}

# ------------------------------
# -            Start
# ------------------------------
initVariables

showHeader 

ensureCleanUpdateSiteRepository
copyOriginUpdateSiteContent  
signNewJarsInUpdateSiteProject
addFilesToUpdateSiteRepo
commitAndPushUpdateSiteRepo



