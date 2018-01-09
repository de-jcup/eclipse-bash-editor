#! /bin/bash
# -*- coding: utf-8 -*-
# LAMP setup (installer)
# Tool for internal use
#
# author: Antonio M. Vigliotti - antoniomaria.vigliotti@gmail.com
# (C) 2015-2017 by SHS-AV s.r.l. - http://www.shs-av.com - info@shs-av.com
# This free software is released under GNU Affero GPL3
#
# *** security options ***
# [RHEL] yum groupinstall 'Development Tools'
# [RHEL] yum install gcc libffi-devel python-devel openssl-devel
# [Debian] apt-get install build-essential
# [Debian] apt-get install libssl-dev libffi-dev python-dev
# pip install pyOpenSSL ndg-httpsclient pyasn1
# pip install cryptography
# ********
# TODO remove: python-paste python-pillow python-tempita
#  remove CentOS6: pyOpenSSL jbigkit-libs libwebp python-six
#  ZSI
# # yum install python-requests python-yaml
# codicefiscale pyxb pypdf for italian localization
# products?
# # oerplib
# flake8, autopep8, pylint, pylint-mccabe, coverage, coveralls, github3.py click
#[RHEL]
# curl --silent --location https://rpm.nodesource.com/setup_5.x | bash -
# yum -y install nodejs
# curl -L https://npmjs.org/install.sh | sh
#[Debian]
# apt-get install nodejs
# apt-get install npm
#[Common]
# npm install -g less
# npm install -g less-plugin-clean-css
#[End]
# per Kaspersky
# yum install kernel-headers
# yum install glibc.i686
#[RHEL] chkconfig
#[Debian] update-rc.d

THIS=$(basename "$0")
TDIR=$(readlink -f $(dirname $0))
for x in "$TDIR" "$TDIR/.." "." ".." "~" "/etc"; do
  if [ -e $x/z0librc ]; then
    . $x/z0librc
    Z0LIBDIR=$x
    Z0LIBDIR=$(readlink -e $Z0LIBDIR)
    break
  fi
done
if [ -z "$Z0LIBDIR" ]; then
  echo "Library file z0librc not found!"
  exit 2
fi
TESTDIR=$(findpkg "" "$TDIR . .." "tests")
RUNDIR=$(readlink -e $TESTDIR/..)

__version__=0.2.36.27


STS_FAILED=1
STS_SUCCESS=0


conf_default() {
    # ALIAS
    local p x
    set_cfg_def "wkhtmltopdf_ReqPkgList" "xorg-x11-fonts-Type1 xorg-x11-fonts-75dpi" "0"
    # Bill of packages
    set_cfg_def "LAMP_security" "libffi-devel pyOpenSSL pyasn1 ndg-httpsclient" "0" "CentOS7"
    set_cfg_def "LAMP_security" "openssl-devel libffi-devel pyOpenSSL pyasn1 ndg-httpsclient" "0" "CentOS6"
    set_cfg_def "LAMP_security" "libffi-dev libssl-dev python-openssl build-essential" "0" "Debian"
    p="setuptools python-virtualenv virtualenv"
    p="$p libreadline6 PYTHON_LIB_XML psycopg2 simplejson xlwt PyYAML"
    p="$p gdata python-ldap pytz python-requests"
    if [ -n "$opt_dev" ]; then
      p="$p python-dev libsasl2-dev libldap2-dev zlib1g-dev libssl-dev"
      p="$p libreadline6-dev libsqlite3-dev tk-dev libgdbm-dev libpcap-dev"
      p="$p liblzma-dev python-yaml mock"
    fi
    set_cfg_def "python_BOP" "$p" "0" "Debian"
    p="setuptools python-virtualenv virtualenv readline"
    p="$p PYTHON_LIB_XML simplejson xlwt PyYAML"
    p="$p gdata python-ldap pytz python-requests"
    if [ -n "$opt_dev" ]; then
      p="$p python-devel openldap-devel zlib-devel bzip2-devel openssl-devel"
      P="$p ncurses-devel readline-devel sqlite-devel tk-devel gdbm-devel"
      p="$p libpcap-devel xz-devel postgresql-devel libxml2-devel"
      p="$p libxslt-devel python-yaml mock"
    fi
    set_cfg_def "python_BOP" "$p psycopg2" "0" "CentOS7"
    set_cfg_def "python_BOP" "$p python-psycopg2" "0" "CentOS6"
    p="ghostscript libart-2.0-2 libcupsfilters1"
    p="$p libcupsimage2 libgs9 libgs9-common libijs-0.35 libjbig2dec0"
    p="$p liblcms2-2 libpaper-utils libpaper1 libpoppler44 libtidy-0.99-0 libwebp5"
    p="$p libwebpmux1 poppler-data poppler-utils Babel python-babel-localedata"
    p="$p python-dateutil decorator==3.4.0 docutils==0.12 feedparser==5.1.3"
    P="$p gevent==1.0.2 python-greenlet python-imaging jinja2==2.7.3 python-mako"
    p="$p python-markupsafe python-openid passlib==1.6.2 python-pil"
    P="$p psutil python-pybabel python-pygments"
    p="$p python-pyinotify pyparsing python-pypdf python-renderpm"
    p="$p reportlab==3.1.44 python-reportlab-accel python-roman python-suds"
    p="$p python-unittest2 python-utidylib python-vobject"
    p="$p python-werkzeug docutils-common docutils-doc wkhtmltopdf"
    if [ -n "$opt_dev" ]; then
      p="$p oerplib erppeek os0 pytok"
    fi
    set_cfg_def "odoo_BOP" "$p" "0" "Debian"
    p="Babel==1.3 dejavu-fonts-common dejavu-sans-fonts fontpackages-filesystem"
    p="$p libjpeg-turbo libtiff libyaml passlib==1.6.2"
    p="$p pyOpenSSL pyparsing beaker python-dateutil docutils==0.12 feedparser==5.1.3 jinja2==2.7.3"
    p="$p python-ldap python-mako python-markupsafe python-openid"
    p="$p psutil reportlab==3.1.44"
    p="$p python-unittest2 urllib3 python-vobject python-werkzeug wkhtmltopdf"
    if [ -n "$opt_dev" ]; then
      p="$p libjpeg-turbo-devel oerplib erppeek os0 pytok"
    fi
    set_cfg_def "odoo_BOP" "$p" "0" "CentOS7"
    p="python-babel dejavu-fonts-common dejavu-sans-fonts fontpackages-filesystem"
    p="$p libjpeg-turbo libtiff libyaml passlib"
    p="$p pyOpenSSL pyparsing beaker python-dateutil python-docutils python-feedparser python-jinja2"
    p="$p python-ldap python-mako python-markupsafe python-openid"
    p="$p psutil python-reportlab"
    p="$p python-unittest2 urllib3 python-vobject python-werkzeug wkhtmltopdf"
    if [ -n "$opt_dev" ]; then
      p="$p libjpeg-turbo-devel oerplib erppeek os0 pytok"
    fi
    set_cfg_def "odoo_BOP" "$p" "0" "CentOS6"
    if [ "$opt_oed" == "odoo" ]; then
      gitrep="odoo/odoo"
      set_cfg_def "odoo_Branch" "6.1 7.0 8.0 9.0 10.0" "0"
    elif [ "$opt_oed" == "oca" ]; then
      gitrep="OCA/OCB"
      set_cfg_def "odoo_Branch" "7.0 8.0 9.0 10.0" "0"
      p="account-closing account-financial-reporting account-financial-tools account-invoicing account-payment bank-payment connector knowledge l10n-italy partner-contact product-attribute reporting-engine report-print-send server-tools stock-logistics-barcode stock-logistics-tracking stock-logistics-warehouse stock-logistics-workflow web webkit-tools website"
      set_cfg_def "odoo_SubPkgList" "$p" "0"
    else
      gitrep="zeroincombenze/OCB"
      set_cfg_def "odoo_Branch" "6.1 7.0 8.0" "0"
      p="account-closing account-financial-reporting account-financial-tools account-invoicing account-payment bank-payment connector knowledge l10n-italy l10n-italy-supplemental partner-contact product-attribute reporting-engine report-print-send server-tools stock-logistics-barcode stock-logistics-tracking stock-logistics-warehouse stock-logistics-workflow web webkit-tools website"
      set_cfg_def "odoo_SubPkgList" "$p" "0"
      set_cfg_def "odoo_upstream" "https://github.com/OCA/$addons.git"
    fi
    set_cfg_def "odoo_cmdname" "NULL" "0"
    set_cfg_def "odoo_git_URL" "https://github.com/$gitrep.git" "0"
    # set_cfg_def "odoo_git_LocalRoot" "/opt/odoo" "0"
    # set_cfg_def "odoo_git_LocalUser" "odoo:odoo" "0"
    set_cfg_def "odoo_confdirs" "/etc/odoo,/var/log/odoo,/var/run/odoo" "0"
    # odoo locale
    p=${gitrep%/*}
    if [ "$p" != "odoo" ]; then
      set_cfg_def "odoo__it_git_URL" "https://github.com/$p/l10n-italy.git" "0"
      set_cfg_def "odoo__be_git_URL" "https://github.com/OCA/l10n-belgium.git" "0"
      set_cfg_def "odoo__ch_git_URL" "https://github.com/OCA/l10n-switzerland.git" "0"
      set_cfg_def "odoo__es_git_URL" "https://github.com/OCA/l10n-spain.git" "0"
      set_cfg_def "odoo__fr_git_URL" "https://github.com/OCA/l10n-france.git" "0"
      set_cfg_def "odoo__nl_git_URL" "https://github.com/OCA/l10n-netherlands.git" "0"
    fi
    # Packages options
    if [ $test_mode -eq 0 ]; then
      set_cfg_def "${THIS}_vfycmd" "/usr/bin/$THIS -V" "0"
    else
      set_cfg_def "${THIS}_cmdname" "$0" "0"
      set_cfg_def "${THIS}_vfycmd" "$0 -V" "0"
    fi
    # set_cfg_def "apache2_cmdname" "apachectl" "0" "Debian"
    set_cfg_def "apache2_LocalRoot" "/var/www/html" "0" "Debian"
    # set_cfg_def "httpd_cmdname" "apachectl" "0" "RHEL"
    set_cfg_def "httpd_LocalRoot" "/var/www/html" "0" "RHEL"
    set_cfg_def "mysql-server_cmdname" "mysql" "0"
    set_cfg_def "mariadb-server_cmdname" "mysql" "0" "CentOS7"
    set_cfg_def "mysql_cmdname" "mysql" "0"
    set_cfg_def "mariadb_cmdname" "mysql" "0" "CentOS7"
    set_cfg_def "wkhtmltopdf_wget_URL" "https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.1"
    if [ "$MACHARCH" != "x86_64" ]; then
      set_cfg_def "wkhtmltopdf_wget_xname" "wkhtmltox-0.12.1_linux-trusty-i386.deb" "0" "Ubuntu14"
      set_cfg_def "wkhtmltopdf_wget_xname" "wkhtmltox-0.12.1_linux-precise-i386.deb" "0" "Ubuntu12"
      set_cfg_def "wkhtmltopdf_wget_xname" "wkhtmltox-0.12.1_linux-centos6-i386.rpm" "0" "CentOS6"
    else
      set_cfg_def "wkhtmltopdf_wget_xname" "wkhtmltox-0.12.1_linux-trusty-amd64.deb" "0" "Ubuntu14"
      set_cfg_def "wkhtmltopdf_wget_xname" "wkhtmltox-0.12.1_linux-precise-amd64.deb" "0" "Ubuntu12"
      set_cfg_def "wkhtmltopdf_wget_xname" "wkhtmltox-0.12.1_linux-centos7-amd64.rpm" "0" "CentOS7"
      set_cfg_def "wkhtmltopdf_wget_xname" "wkhtmltox-0.12.1_linux-centos6-amd64.rpm" "0" "CentOS6"
    fi
    if [ "$DISTO" == "CentOS6" ]; then
      set_cfg_def "python-dateutil_wget_URL" "https://pypi.python.org/packages/source/p/python-dateutil/" "0"
      set_cfg_def "python-dateutil_wget_xname" "python-dateutil-1.5.tar.gz" "0"
      set_cfg_def "python-dateutil_wget_md5" "#md5=0dcb1de5e5cad69490a3b6ab63f0cfa5" "0"
    fi
    set_cfg_def "pandoc_git_URL" "https://github.com/jgm/pandoc.git" "0"
    set_cfg_def "pandoc_git_LocalRoot" "/opt/pandoc" "0"
    set_cfg_def "pandoc_SubPkgList" "." "0"
    # Service names
    # set_cfg_def "vsftpd_svcname" "vsftpd" "0"
    set_cfg_def "iptables_svcname" "iptables" "0"
    # set_cfg_def "apache2_svcname" "apache2" "0" "Debian"
    # set_cfg_def "httpd_svcname" "httpd" "0" "RHEL"
    set_cfg_def "mysql_svcname" "mysql" "0" "Debian"
    set_cfg_def "mariadb_svcname" "mariadb" "0" "CentOS7"
    set_cfg_def "mariadb-server_svcname" "mariadb" "0" "CentOS7"
    set_cfg_def "mysql_svcname" "mysqld" "0" "CentOS6"
    set_cfg_def "odoo_svcname" "odoo-server" "0"
    # TCP & UDP Ports
    # set_cfg_def "TCP_httpd" "80 443" "0" "RHEL"
    # set_cfg_def "TCP_apache2" "80 443" "0" "Debian"
    # set_cfg_def "TCP_vsftpd" "21" "0"
    set_cfg_def "TCP_odoo" "8069" "0"
    # Configuration files
    p="/etc/httpd /etc/apache2; conf"
    # set_cfg_def "apache2_FINDCFN" "$p" "0" "Debian"
    # set_cfg_def "apache2_confn" "apache2.conf" "0" "Debian"
    # set_cfg_def "httpd_FINDCFN" "$p" "0" "RHEL"
    # set_cfg_def "httpd_confn" "httpd.conf" "0" "RHEL"
    p="/var/lib /etc;postgresql pgsql;9.4 9.3 9.2 9.1 9.0 8.4;data main"
    set_cfg_def "postgresql_FINDCFN" "$p" "0" "Debian"
    set_cfg_def "postgresql_confn" "pg_hba.conf" "0" "Debian"
    set_cfg_def "postgresql-server_FINDCFN" "$p" "0" "RHEL"
    set_cfg_def "postgresql-server_confn" "pg_hba.conf" "0" "RHEL"
    p="/etc/odoo /etc/openerp;"
    set_cfg_def "odoo_FINDCFN" "$p" "0"
    set_cfg_def "odoo_confn" "odoo-server.conf" "0"
}


store_cfg_param_value() {
#store_cfg_param_value(tid key value [-d|-f|] [-D|1] [section])
    local p tid=$1
    if [[ " xyz TCP " =~ [[:space:]]$2[[:space:]] ]]; then
      p="${2}_$6"
    elif [[ " BOP BOP_GRF_ADD binPkgList build_fileignore build_PkgList build_PKGPATH\
 build_with_z0libr build_with_odoorc cmdname confn etcPkgList FINDCFN git_LocalDir\
 git_LocalRoot git_LocalUser git_URL init_svcname install_LocalRoot install_LocalTmp\
 install_md5 install_xname ReqPkgList svcname SubPkgList with_z0libr xtlcmd xtlcmd_install\
 xtlcmd_remove wget_URL wget_xname " =~ [[:space:]]$2[[:space:]] ]]; then
      p="${6}_$2"
    else
      p="$2"
      [[ "${2:0:6}" == "REAL_" ]] && tid="0"
    fi
    [[ $opt_dbg -gt 0 ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " ">>> CFG_set \"$tid\" \"$p\" \"$3\" \"$4\" \"$5\" $6 <- store_cfg_param_value($1 $2 $3 $4 $5 $6);">>~/$THIS.his
    CFG_set "$tid" "$p" "$3" "$4" $5
}

get_conf_pkg() {
# get_conf_pkg (file sample [section] ][OPTS])
    [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "get_conf_pkg(\"$1\" \"$2\" \"$3\" $4)">>~/$THIS.his
    local opt_dev
    local confn=$1
    [[ $4 =~ D ]] && opt_dev="-D"
    if [ -z "$confn" ]; then
      if [ "${2: -7}" == ".sample" ]; then
        confn="${2:0: -7}"
      else
        confn=$2
      fi
    fi
    local tid=1
    while [ $tid -le $conf_level ]; do
      if [ "$confn" == "${CONF_FNS[$tid]}" ]; then
        [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return #get_conf_pkg">>~/$THIS.his
        return
      fi
      ((tid++))
    done
    ((conf_level++))
    if  [ $conf_level -gt 3 ]; then
      tid=3
    else
      tid=$conf_level
    fi
    CONF_FNS[$conf_level]=$confn
    [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  link_cfg \"$1\" \"$2\" \"$tid\" \"$3\" $opt_dev">>~/$THIS.his
    link_cfg "$1" "$2" "$tid" "$3" "$opt_dev"
    [[ $opt_dev == "-D" && -n "$2" ]] && link_cfg "$1" "$1" "$tid" "${3}DEV_" "$opt_dev"
    init_cfg_pkg
    [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return #get_conf_pkg">>~/$THIS.his
}

clr_conf_pkg() {
    if  [ $conf_level -gt 0 ]; then
      if  [ $conf_level -le 3 ]; then
        CFG_init $conf_level
      fi
      CONF_FNS[$conf_level]=
      ((conf_level--))
    fi
}

set_from_conf_pkg() {
# set_from_conf_pkg (pkgname [confn] [opts]])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "set_from_conf_pkg($1 \"$2\" $3)">>~/$THIS.his
    local FCONF FCONFDEF SAMPLE sdir
    if [ $test_mode -gt 0 ]; then
      sdir="$TESTDIR $TDIR"
    elif [ -n "$2" ]; then
      sdir="$TDIR /etc/lisa /etc"
    else
      sdir=". $TDIR /etc/lisa /etc"
    fi
    local pkgname=$(get_realname $1)
    if [ -n "$2" ]; then
      if [ -n "$opt_conf" -a -f "$opt_conf" ]; then
        FCONF=$opt_conf
      else
        FCONF=$2
      fi
    else
      FCONF=${1}.lish
    fi
    FCONFDEF=$FCONF.sample
    PKG_CONF=$(findpkg "$FCONF" "$sdir")
    SAMPLE=$(findpkg "$FCONFDEF" "$sdir")
    if [ -z "$PKG_CONF" -a -z "$SAMPLE" ]; then
      FCONF=$pkgname
      FCONFDEF=$pkgname.sample
      [ -n "$FCONF" ] && PKG_CONF=$(findpkg "$FCONF.lish" "$sdir")
      [ -n "$FCONF" ] && SAMPLE=$(findpkg "$FCONFDEF" "$sdir")
    fi
    if [ -z "$PKG_CONF" -a -z "$SAMPLE" ]; then
      if [ "${FCONF: -7}" == "-server" ]; then
        FCONF=${FCONF:0: -7}
        PKG_CONF=$(findpkg "$FCONF.lish" "$sdir")
      elif [ "${FCONF: -1}" == "d" ]; then
        FCONF=${FCONF:0: -1}
        PKG_CONF=$(findpkg "$FCONF.lish" "$sdir")
      elif [ "${FCONF: -4}" == "-dev" ]; then
        FCONF=${FCONF:0: -4}
        PKG_CONF=$(findpkg "$FCONF.lish" "$sdir")
      elif [ "${FCONF: -6}" == "-devel" ]; then
        FCONF=${FCONF:0: -6}
        PKG_CONF=$(findpkg "$FCONF.lish" "$sdir")
      fi
      if [ -n "$PKG_CONF" ]; then
        FCONFDEF=${FCONF}.sample
        SAMPLE=$(findpkg "$FCONFDEF" "$sdir")
      fi
    fi
    if [ -z "$PKG_CONF" -a -z "$SAMPLE" ]; then
      PKG_CONF=$(findpkg "lisa.lish" "$sdir")
      SAMPLE=$(findpkg "lisa.lish.sample" "$sdir")
    fi
    get_conf_pkg "$PKG_CONF" "$SAMPLE" "$1" $3
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return #set_from_conf_pkg">>~/$THIS.his
}

get_prm_value() {
# get_prm_value(prmname [pkgname|list])
# return: prmvalue; -> disco, vist
    local p
    local x
    if [ -z "$1" ]; then
      p=
      echo $p
      return
    else
      for tid in 3 2 1 0; do
        p=$(get_cfg_value $tid $1)
        if [ -n "$p" ]; then break; fi
      done
      if [ "$p" == "None" -o "$p" == "NULL" ]; then
        echo ""
        return
      fi
    fi
    if [ -z "$p" ]; then
      if [ "$1" != "-server_svcname" -a "${1: -15}" == "-server_svcname" ]; then
        if [ "$FH" == "RHEL" ]; then
          p="${1:0: -15}d"
        else
          p="${1:0: -15}"
        fi
      elif [ "$1" != "_cmdname" -a "${1: -8}" == "_cmdname" ]; then
        x="${1:0: -8}"
        if ! $(is_virtualname $x); then
          p="$x"
          if [ "$x" != "pip" ] && [[ " $PIP_PYSTD_PKGS " =~ [[:space:]/]$x[[:space:]/] ]]; then
            p=""
          fi
        else
          p=""
        fi
      elif [ "$1" != "xtlcmd" -a "${1: -7}" == "_xtlcmd" ]; then
        x="${1:0: -7}"
        if $(is_virtualname $x); then
          p="lisa"
        elif [[ " $PIP_PKGS " =~ [[:space:]]$x[[:space:]] ]]; then
          p="pip"
        elif [[ " $PIP_PYSTD_PKGS " =~ [[:space:]/]$x[[:space:]/] ]]; then
          p="pip"
        elif [[ " $GIT_PKGS " =~ [[:space:]]$x[[:space:]] ]]; then
          p="git"
        elif [[ " $WGET_PKGS " =~ [[:space:]]$x[[:space:]] ]]; then
          p="wget"
        elif [[ " $YUM_PKGS " =~ [[:space:]]$x[[:space:]] ]]; then
          p="yum"
        elif [[ " $APT_PKGS " =~ [[:space:]]$x[[:space:]] ]]; then
          p="apt-get"
        elif [[ " $PIP_PYSTD_PKGS " =~ [[:space:]]$x[[:space:]/] ]] || [[ " $STD_PKGS " =~ [[:space:]]$x[[:space:]] ]]; then
          if [ "$FH" == "RHEL" ]; then
            p="yum"
          elif [ "$FH" == "Debian" ]; then
            p="apt-get"
          else
            p="#"
          fi
        else
          p=
        fi
      elif [ "$1" != "_xtlcmd_install" -a "${1: -15}" == "_xtlcmd_install" ]; then
        x="${1:0: -15}"
        if $(is_virtualname $x); then
          p="lisa"
        elif [[ " $PIP_PYSTD_PKGS " =~ [[:space:]/]$x[[:space:]/] ]]; then
          if [ "$FH" == "RHEL" ]; then
            p="yum"
          elif [ "$FH" == "Debian" ]; then
            p="apt-get"
          else
            p="#"
          fi
        fi
      elif [ "$1" != "stscmd" -a "${1: -7}" == "_stscmd" ]; then
        x="${1:0: -7}"
        p=$(get_prm_value "${x}_xtlcmd")
        p=${p//yum/rpm}
        p=${p//apt-get/dpkg-query}
      elif [ "$1" != "_stscmd_install" -a "${1: -15}" == "_stscmd_install" ]; then
        x="${1:0: -15}"
        p=$(get_prm_value "${x}_xtlcmd_install")
        p=${p//yum/rpm}
        p=${p//apt-get/dpkg-query}
      elif [ "$1" != "_vfycmd" -a "${1: -7}" == "_vfycmd" ]; then
        p="${1:0: -7}"
        x=$(basename $p)
        if [ "$x" != "$p" ]; then
          p=$(get_cfg_value $tid ${x}_vfycmd)
        else
          p=
        fi
        if [ -z "$p" ]; then
          if $(is_virtualname $x); then
            p=""
          else
            x=$(get_prm_value "${x}_cmdname")
            if [ -z "$x" ]; then
              p=""
            else
              if [[ " $NOINQ " =~ [[:space:]]$x[[:space:]] ]]; then
                p=
              elif [[ " $VFYLOW " =~ [[:space:]]$x[[:space:]] ]]; then
                p="$x -v"
              elif [[ " $VFYUPP " =~ [[:space:]]$x[[:space:]] ]]; then
                p="$x -V"
              elif [[ " $VFYPSX " =~ [[:space:]]$x[[:space:]] ]]; then
                p="$x --version"
              else
                p=""
              fi
            fi
          fi
        fi
      fi
    fi
    if [ "$1" != "_BOP" -a "${1: -4}" == "_BOP" -a $opt_grf -ne 0 ]; then
      x=$(get_cfg_value $tid "${1}_GRF_ADD")
      p=$p,$x
    fi
    if [ -n "$2" ]; then
      p="${p//,/ }"
      if [ "$2" != "list" ]; then
        p="$(echo "$p"|sed -e s:\${pkgname}:$2:g)"
      fi
      echo $p
    else
      echo $p
    fi
}

init_cfg_pkg() {
# init_cfg_pkg (pkgname)::FCONF,NAME*,VFY*,STD*,PIP*,NOINQ*
# return:
    local l p r x
    LAMP=$(get_prm_value "LAMP" list)
    NAME_VIRTUAL="$(get_prm_value NAME_VIRTUAL list)"
    NOINQ="$(get_prm_value NOINQ list)"
    VFYLOW="$(get_prm_value VFYLOW list)"
    VFYUPP="$(get_prm_value VFYUPP list)"
    VFYPSX="$(get_prm_value VFYPSX list)"
    VFYE2NL="$(get_prm_value VFYE2NL list)"
    VFYERR="$(get_prm_value VFYERR list)"
    PIP_PKGS="$(get_prm_value PIP_PKGS list)"
    GIT_PKGS="$(get_prm_value GIT_PKGS list)"
    WGET_PKGS="$(get_prm_value WGET_PKGS list)"
    STD_PKGS="$(get_prm_value STD_PKGS list) EVERYTHINGS"
    YUM_PKGS="$(get_prm_value YUM_PKGS list)"
    APT_PKGS="$(get_prm_value APT_PKGS list)"
    PIP_PYSTD_PKGS="$(get_prm_value PIP_PYSTD_PKGS list)"
}

print_title() {
    if [ $opt_verbose -gt 0 -a $test_mode -eq 0 ]; then
      wlog "$1"
    fi
}

verbose_msg() {
    if [ $opt_verbose -gt 0 -a $test_mode -eq 0 ]; then
      elog "$@"
    fi
}

test_msg() {
    if [ $test_mode -eq 0 ]; then
      echo "$@"
    else
      elog "$@"
    fi
}

ending_msg() {
    if [ $test_mode -eq 0 ]; then
      echo "See $FLOG for traced informations"
    fi
}

get_iter() {
# get_iter(pkgname [OPTS])
# return: <pkg_list>
  if [ -z "$1" ]; then
    local iter="$1"
  else
    local p tid
    for tid in 3 2 1 0; do
      p=$(get_cfg_value $tid $1)
      if [ -n "$p" ]; then break; fi
    done
    if [ -n "$p" ]; then
      local iter="${p//,/ }"
    else
      local iter="$1"
    fi
    if $(is_virtualname $1); then
      if [[ " $iter " =~ [[:space:]]$1[[:space:]] ]]; then
        :
      else
        local iter="$1 $iter"
      fi
    fi
  fi
  [[ $2 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "$iter=get_iter($1 $2)">>~/$THIS.his
  echo "$iter"
}

enable_port() {
# enable_port(port prot [OPTS])
    local port=$1
    local prot=$2
    if [ "$prot" != "tcp" -a "$prot" != "upd" ]; then
       prot=tcp
    fi
    cmd="iptables -A INPUT -p $prot -m state --state NEW -m $prot --dport $port -j ACCEPT"
    if [[ $3 =~ I ]]; then
      x="port_$prot_$port"
      cmd="# $cmd"
    elif [[ $3 =~ J ]]; then
      x=""
    elif [[ " $SIMULATE_YES " =~ [[:space:]]port_${prot}_${port}[[:space:]] ]]; then
      x="port_$prot_$port"
      cmd="# $cmd"
    elif [[ " $SIMULATE_NO " =~ [[:space:]]port_${prot}_${port}[[:space:]] ]]; then
      x=""
    else
      x=$(iptables -S 2>/dev/null|grep "\-p *$prot *.*\-dport *$port" 2>/dev/null)
    fi
    if [ -z "$x" ]; then
      run_traced "$cmd"
    fi
}

disable_port() {
    local port=$1
    local prot=$2
    if [ "$prot" != "tcp" -a "$prot" != "upd" ]; then
       prot=tcp
    fi
    cmd="iptables -D INPUT -p $prot -m state --state NEW -m $prot --dport $port -j ACCEPT"
    if [[ $3 =~ I ]]; then
      x="port_$prot_$port"
      cmd="# $cmd"
    elif [[ $3 =~ J ]]; then
      x=""
    elif [[ " $SIMULATE_YES " =~ [[:space:]]port_${prot}_${port}[[:space:]] ]]; then
      x="port_$prot_$port"
      cmd="# $cmd"
    elif [[ " $SIMULATE_NO " =~ [[:space:]]port_${prot}_${port}[[:space:]] ]]; then
      x=""
    else
      x=$(iptables -S 2>/dev/null|grep "\-p *$prot *.*\-dport *$port" 2>/dev/null)
    fi
    if [ "$x" ]; then
      run_traced "$cmd"
    fi
}

get_arch() {
    if [ "$1" == "CentOS7" -o "$1" == "CentOS" -o "$1" == "RHEL" ]; then
      export DISTO="CentOS7"
      export FH="RHEL"
      LXCORE=
      MACHARCH="x86_64"
    elif [ "$1" == "CentOS6" ]; then
      export DISTO="CentOS6"
      export FH="RHEL"
      LXCORE=
      MACHARCH="i686"
    elif [ "$1" == "Ubuntu14" -o "$1" == "Ubuntu"  -o "$1" == "Debian" ]; then
      export DISTO="Ubuntu14"
      export FH="Debian"
      LXCORE="trusty"
      MACHARCH="x86_64"
    elif [ "$1" == "Ubuntu12" ]; then
      export DISTO="Ubuntu12"
      export FH="Debian"
      LXCORE="precise"
      MACHARCH="i686"
    else
      export FH=$(xuname "-f")
      local x=$(xuname "-v")
      local v=$(echo $x|awk -F. '{print $1}')
      export DISTO=$(xuname "-d")$v
      LXCORE=$(xuname "-c")
      MACHARCH=$(xuname "-m")
      if [ -n "$1" ]; then
        echo "!!Invalid $1 architecture: used $DISTO instead!!"
      fi
    fi
    XU_FH="$FH"
    XU_DISTO="$DISTO"
}

mkdir_traced() {
# mkdir_traced(dir [user])
    if [ ! -d $1  -o $test_mode -gt 0 ]; then
      run_traced "mkdir -p $1"
      if [ -n "$2" ]; then
         run_traced "chown $2 $1/"
      fi
      run_traced "chmod u+rwx,g=rx,o=rx $1/"
    fi
}

is_virtualname() {
    local pkgname=$1
    local sts=$STS_FAILED
    if [ -n "$pkgname" -a "$pkgname" != "." ]; then
      if [[ " $NAME_VIRTUAL " =~ [[:space:]]$pkgname[[:space:]] ]]; then
        sts=$STS_SUCCESS
      fi
    fi
    return $sts
}

get_realaction(){
# get_realaction(action)
    if [ "${1:0:4}" == "pre_" ]; then
      local act=${1:4}
    elif [ "${1:0:3}" == "do_" ]; then
      local act=${1:3}
    elif [ "${1:0:5}" == "post_" ]; then
      local act=${1:5}
    else
      local act=$1
    fi
    echo $act
}

get_pfxaction(){
# get_realaction(action)
    if [ "${1:0:4}" == "pre_" ]; then
      local pfx="pre_"
    elif [ "${1:0:3}" == "do_" ]; then
      local pfx="do_"
    elif [ "${1:0:5}" == "post_" ]; then
      local pfx="post_"
    else
      local pfx=
    fi
    echo $pfx
}

get_realname() {
# get_realname(pkgname [action] [OPTS])
# return: realname
    local p pkgname tid
    if [ "$1" == "." ]; then
      p="EVERYTHINGS"
    else
      pkgname=$(echo "$1"|grep -Eo '[^!<=>]*'|head -n1)
      for tid in 0 1 2 3; do
        p=$(get_cfg_value $tid REAL_$pkgname)
        if [ -n "$p" ]; then break; fi
      done
    fi
    if [ -n "$p" ]; then
      pkgname=$p
    elif [ "$2" == "install" -o "$2" == "remove" ] && [[ " $PIP_PYSTD_PKGS " =~ [[:space:]][^/[:space:]]+\/$1[[:space:]] ]]; then
      IFS=/ read l r <<<$BASH_REMATCH
      if [ "$l" != "$r" ]; then
        pkgname=$l
      fi
    elif [ "$2" == "update" ] && [[ " $PIP_PYSTD_PKGS " =~ [[:space:]]$1\/[^/[:space:]]+[[:space:]] ]]; then
      IFS=/ read l r <<<$BASH_REMATCH
      if [ "$l" != "$r" ]; then
        pkgname=$r
      fi
    fi
    if [ -z "$pkgname" ]; then
      if [ "$FH" == "RHEL" ]; then
        p=$(get_prm_value REAL_${1}__Debian)
        if [ "${p: -4}" == "-dev" ]; then
          pkgname="${p}el"
        fi
      elif [ "$FH" == "Debian" ]; then
        p=$(get_prm_value REAL_${1}__RHEL)
        if [ "${p: -6}" == "-devel" ]; then
          pkgname="${p:0: -2}"
        fi
      fi
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " "  "  $pkgname=get_realname($1 $2 $3)">>~/$THIS.his
    echo $pkgname
}

get_specimen() {
# get_specimen(pkgname [action] [OPTS])
# specimen means: original_name left_op left_version right_op right_version best_version xtlcmd
# return: specimen
    if [ "$1" == "." ]; then
      local specimen="~~~~~~"
    else
      local c=$(echo "$1"|grep -Eo '[!<=>]*'|wc -l)
      local i pkg pkgname lop rop lreqver rreqver xtlcmd op p
      pkg=$(echo "$1"|grep -Eo '[^!<=>]*'|head -n1)
      i=1
      while ((i<=c)); do
        op=$(echo "$1"|grep -Eo '[!<=>]*'|head -n$i|tail -n1)
        ((i++))
        p=$(echo "$1"|grep -Eo '[^!<=>]*'|head -n$i|tail -n1)
        if [ "$op" == "!!" ]; then
          xtlcmd=$p
        elif [ -z "$lreqver" ]; then
          lreqver=$p
          lop=$op
        elif [ -z "$rreqver" ]; then
          rreqver=$p
          rop=$op
        fi
      done
      pkgname=$(get_realname $1 "$2" $3 $4 "$5")
      p="$(get_prm_value ${pkgname}_Branch)"
      local branch=
      if [ -n "$p" ]; then
        for i in $p; do
          if $(cmp_ver "$i" "$lop" "$lreqver") && $(cmp_ver "$i" "$rop" "$rreqver"); then
            branch=$i
          fi
        done
      fi
      local specimen="$pkg~$lop~$lreqver~$rop~$rreqver~$branch~$xtlcmd"
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " "  "$specimen=get_specimen($1 $2 $3)">>~/$THIS.his
    echo "$specimen"
}

get_odoo_full_ver() {
    if [ "$1" == "6" ]; then
      echo "6.1"
    else
      echo "$1.0"
    fi
}

get_pkg_ver_inq() {
# get_pkg_ver_inq(pkgname [OPTS])
# return: ->  pkgversion
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "get_pkg_ver_inq($1 $2)">>~/$THIS.his
    local pkgname=$1
    local X="VER__${1//-/_}"
    local cmdname p pkgversion= vfycmd x
    if [ -n "${!X}" ]; then
      pkgversion="${!X}"
    elif [[ $2 =~ J ]]; then
      :
    else
      vfycmd=$(get_prm_value "${pkgname}_vfycmd")
      cmdname=$(get_prm_value "${pkgname}_cmdname")
      if [ -z "$vfycmd" ]; then
        [ -n "$cmdname" ] && vfycmd=$(get_prm_value "${cmdname}_vfycmd")
      fi
      if [ -n "$vfycmd" ]; then
        x="$(echo $vfycmd|head -n1|awk -F\| '{print $1}'|awk -F'2>' '{print $1}'|awk -F'&>' '{print $1}')"
        p=$($x &>/dev/null)
        if [ $? -eq $STS_SUCCESS ]; then
          if [[ " $VFYERR " =~ [[:space:]]$cmdname[[:space:]] ]]; then
            pkgversion="$($vfycmd 2>&1 |head -n1)"
          elif [[ " $VFYE2NL " =~ [[:space:]]$cmdname[[:space:]] ]]; then
            pkgversion="$($vfycmd|head -n1)"
          else
            pkgversion="$($vfycmd 2>/dev/null|head -n1)"
          fi
        elif [[ $2 =~ I ]]; then
          pkgversion="($pkgname).($vfycmd)"
        fi
      fi
      if [ "$pkgversion" ]; then
        declare $X="$pkgversion"
      fi
    fi
    [[ $2 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " "  "  $pkgversion=get_pkg_ver_inq($1 $2)">>~/$THIS.his
    echo $pkgversion
}

get_pkg_ver_pip() {
# get_pkg_ver_pip(pkgname [OPTS])
# return: pkgversion
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "get_pkg_ver_pip($1 $2)">>~/$THIS.his
    local pkgname="$1"
    local stscmd=pip
    local pkgversion=
    local x=
    local X="VER__${1//-/_}"
    if [ -n "${!X}" ]; then
      pkgversion="${!X}"
    elif [[ $2 =~ J ]]; then
      :
    else
      x="$($stscmd show $pkgname)"
      if [ $? -eq $STS_SUCCESS ]; then
        pkgversion="$($stscmd show $pkgname|grep ^[Vv]ersion|awk -F: '{print $2}'|tr -d ', \r\n\(\)')"
      fi
      if [[ $2 =~ I ]]; then
        if [ -z "$pkgversion" ]; then
          pkgversion="($pkgname).(pip)"
        fi
      fi
      if [ "$pkgversion" ]; then
        declare $X="$pkgversion"
      fi
    fi
    [[ $2 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " "  "  $pkgversion=get_pkg_ver_pip($1 $2)">>~/$THIS.his
    echo $pkgversion
}

get_pkg_ver_rpm() {
# get_pkg_ver_rpm(pkgname [OPTS])
# return: pkgversion
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "get_pkg_ver_rpm($1 $2)">>~/$THIS.his
    local pkgname="$1"
    local stscmd=$(get_prm_value "${1}_stscmd")
    local pkgversion=
    local x=
    local X="VER__${1//-/_}"
    if [ -n "${!X}" ]; then
      pkgversion="${!X}"
    elif [[ $2 =~ J ]]; then
      :
    else
      x=$($stscmd -q --qf '%{VERSION}' $pkgname 2>/dev/null)
      if [ $? -eq $STS_SUCCESS ]; then
        pkgversion="$x"
      fi
      if [[ $2 =~ I ]]; then
        if [ -z "$pkgversion" ]; then
          pkgversion="($pkgname).(rpm)"
        fi
      fi
      if [ "$pkgversion" ]; then
        declare $X="$pkgversion"
      fi
    fi
    [[ $2 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " "  "  $pkgversion=get_pkg_ver_rpm($1 $2)">>~/$THIS.his
    echo $pkgversion
}

get_pkg_ver_dpkg() {
# get_pkg_ver_dpkg(pkgname [OPTS])
# return: pkgversion
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "get_pkg_ver_dpkg($1 $2)">>~/$THIS.his
    local pkgname="$1"
    local stscmd=$(get_prm_value "${1}_stscmd")
    local pkgversion=
    local x=
    local X="VER__${1//-/_}"
    if [ -n "${!X}" ]; then
      pkgversion="${!X}"
    elif [[ $2 =~ J ]]; then
      :
    else
      x="$(${stscmd}-query -f='${Status}' -W $pkgname 2>/dev/null|awk '{print $3}')"
      sts=$?
      if [ $sts -eq $STS_SUCCESS -a "$x" != "installed" ]; then
        sts=$STS_FAILED
      fi
      if [ $sts -eq $STS_SUCCESS ]; then
        pkgversion="$(${stscmd}-query -f='${Version}' -W $pkgname 2>/dev/null|awk -F\- '{print $1}')"
      fi
      if [[ $2 =~ I ]]; then
        if [ -z "$pkgversion" ]; then
          pkgversion="($pkgname).(dkpg)"
        fi
      fi
      if [ "$pkgversion" ]; then
        declare $X="$pkgversion"
      fi
    fi
    [[ $2 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " "  "  $pkgversion=get_pkg_ver_dkpg($1 $2)">>~/$THIS.his
    echo $pkgversion
}


get_pkg_ver_git() {
# get_pkg_ver_git(pkgname [OPTS] [param] [SPECIMEN])
# return: pkgversion
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "get_pkg_ver_git($1 $2)">>~/$THIS.his
    local pkgname="$1"
    local pkgversion=
    local x=
    local X="VER__${1//-/_}"
    if [ -n "${!X}" ]; then
      pkgversion="${!X}"
    elif [[ $2 =~ J ]]; then
      :
    else
      # local xtlcmd=git
      # local git_opts=""
      # local s sts pkg_subm subpkg pkg_URL git_opts pkg_user pkg_branch pkg lop lreqver rop rreqver branch x p
      # IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$4"
      # pkg_URL=$(get_full_URL "$1" $2 $3 $4 "$5")
      # subpkg=$(get_dir_URL "$1" $2 $3 $4 "$5")
      # local pkg_branch="$(get_prm_value ${pkgname}_Branch)"
      # if [ -n "$pkg_URL" ]; then
        do_git_localroot version $1 $2 $3 $4 "$5"
        if [ $? -eq $STS_SUCCESS ]; then
          # if [ -n "$pkg_branch" ]; then
          #   pkgversion=$(basename $PWD)
          # else
          #   pkgversion="N/A"
          # fi
          git status &>/dev/null
          if [ $? -eq 128 ]; then
            pkgversion=
          else
            pkgversion=$(git symbolic-ref --short HEAD)
          fi
        fi
      # fi
    fi
    [[ $2 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  $pkgversion=get_pkg_ver_git($1 $2 $3 $4 $5)">>~/$THIS.his
    echo $pkgversion
}

get_pkg_ver() {
# get_pkg_ver(pkgname [OPTS] [param] [SPECIMEN])
# return: pkgversion
    [[ $2 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "get_pkg_ver($1 $2 $3 $4 $5)">>~/$THIS.his
    local pkgname=$1
    local cmd pkgversion stscmd
    local ij
    if [ "$1" == "EVERYTHINGS" ]; then
      pkgversion="# $pkgname N/A"
    elif [[ " $DETECTED_NO " =~ [[:space:]]$1[[:space:]] ]]; then
      pkgversion=
      ij=J
    elif [[ " $DETECTED_YES " =~ [[:space:]]$1[[:space:]] ]]; then
      ij=I
      pkgversion=$(get_pkg_ver_inq $1 ${2}$ij)
    elif [[ " $SIMULATE_NO " =~ [[:space:]]$1[[:space:]] ]]; then
      pkgversion=
      ij=J
    elif [[ " $SIMULATE_YES " =~ [[:space:]]$1[[:space:]] ]]; then
      ij=I
      pkgversion=$(get_pkg_ver_inq $1 ${2}$ij)
    else
      pkgversion=$(get_pkg_ver_inq $1 $2)
    fi
    if [ -z "$pkgversion" -a "$ij" != "J" ]; then
      if $(is_virtualname $pkgname); then
        pkgversion="# $pkgname N/A"
      else
        stscmd=$(get_prm_value "${1}_stscmd")
        if [ "$stscmd" == "dpkg-query" ]; then
          cmd="get_pkg_ver_dpkg"
        else
          cmd="get_pkg_ver_$stscmd"
        fi
        if [ "$(type -t $cmd)" == "function" ]; then
          pkgversion=$($cmd $1 $2$ij "$3" "$4")
        fi
      fi
    fi
    [[ $2 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  $pkgversion=get_pkg_ver($1 $2)">>~/$THIS.his
    echo $pkgversion
}

expand_param() {
# expand_param(value,version)
    local x="${1//\$\{version\}/$2}"
    echo "$x"
}

get_act_xtlcmd() {
# get_act_xtlcmd(pkgname [action] [OPTS] [param] [SPECIMEN])
    local pkg lop lreqver rop rreqver branch x xtlcmd
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    if [ -n "$x" ]; then
      xtlcmd=$x
    elif [  -n "$2" ]; then
      xtlcmd=$(get_prm_value "${1}_xtlcmd_${2}")
    fi
    if [ -z "$xtlcmd"  -a "$2" == "remove" ]; then
      xtlcmd=$(get_prm_value "${1}_xtlcmd_install")
    fi
    if [ "$xtlcmd" == "$1" ] && [ "$2" == "install" -o "$2" == "remove" ]; then
      xtlcmd=
    fi
    if [ -z "$xtlcmd" ]; then
      xtlcmd=$(get_prm_value "${1}_xtlcmd")
    fi
    if [ "$xtlcmd" == "$1" ] && [ "$2" == "install" -o "$2" == "remove" ]; then
      xtlcmd=
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  $xtlcmd=get_act_xtlcmd($1 $2 $3)">>~/$THIS.his
    echo "$xtlcmd"
}

get_all_xtlcmd() {
# get_all_xtlcmd(action pkgname OPTS [param] [SPECIMEN])
    local x xtlcmd
    x=$(get_act_xtlcmd $2 install "$3" "$4" "$5")
    xtlcmd=$(get_act_xtlcmd $2 "" "$3" "$4" "$5")
    if [ -n "$x" -a "$x" != "$xtlcmd" ]; then
      xtlcmd="$x/$xtlcmd"
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  $xtlcmd=get_all_xtlcmd($1 $2 $3 $4 $5)">>~/$THIS.his
    echo $xtlcmd
}

get_full_xtlcmd() {
# get_full_xtlcmd(action pkgname OPTS [param] [SPECIMEN])
    local xtlcmd=$(get_act_xtlcmd $2 "$1" "$3" "$4" "$5")
    local pkg lop lreqver p rop rreqver branch x pkgURL subpkg
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    p=" $2"
    if [ "$2" == "EVERYTHINGS" ]; then
      p=""
    fi
    # if [ -z "$xtlcmd" -a -n "$x" ]; then
    #   xtlcmd=$x
    # fi
    pkgURL=$(get_full_URL "$1" "$2" "$3" "$4" "$5")
    [ -n "$pkgURL" ] && p=" $pkgURL"
    if [ "$xtlcmd" == "yum" ]; then
      if [[ $3 =~ y ]]; then xtlcmd="$xtlcmd -y"; fi
      xtlcmd="$xtlcmd $1$p"
    elif [ "$xtlcmd" == "apt-get" ]; then
      if [[ $3 =~ y ]]; then xtlcmd="$xtlcmd -y"; fi
      xtlcmd="$xtlcmd ${1/update/upgrade}$p"
    elif [ "$xtlcmd" == "pip" ]; then
      if [ "$1" == "update" ]; then
        xtlcmd="$xtlcmd install$p$lop$lreqver$rop$rreqver --upgrade"
      else
        xtlcmd="$xtlcmd ${1/remove/uninstall}$p$lop$lreqver$rop$rreqver"
      fi
    fi
    if [[ $3 =~ I ]]; then
      if [ "$1" == "install" ]; then
        xtlcmd="# $xtlcmd"
      fi
    elif [[ $3 =~ J ]]; then
      if [ "$1" == "update" -o "$1" == "remove" ]; then
        xtlcmd="# $xtlcmd"
      fi
    elif [ -z "$xtlcmd" ]; then
      xtlcmd="#"
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "$xtlcmd=get_full_xtlcmd($1 $2 $3)">>~/$THIS.his
    echo "$xtlcmd"
}

get_full_svccmd() {
# get_full_svccmd(action svcname OPTS)
    local servicecmd=$(get_realname "OSSERVICE" "" $3)
    if [ "$servicecmd" == "service" ]; then
      local cmd="$servicecmd $2 $1"
    else
      local cmd="$servicecmd $1 $2"
    fi
    if [[ $3 =~ I ]]; then
      if [ "$1" == "start" ]; then
        cmd="# $cmd"
      fi
    elif [[ $3 =~ J ]]; then
      if [ "$1" == "restart" -o "$1" == "stop" ]; then
        cmd="# $cmd"
      fi
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "$cmd=get_full_svccmd($1 $2 $3)">>~/$THIS.his
    echo "$cmd"
}

get_full_autosvc() {
# get_full_autosvc(on|off svcname OPTS)
    local servicecmd=$(get_realname "OSSERVICE" "" $3)
    local cmd svccmd svcsts=$1
    if [ "$servicecmd" != "service" ]; then
      [ "$1" == "on" ] && svcsts="enable"
      [ "$1" == "off" ] && svcsts="disable"
      cmd="$servicecmd $svcsts $2"
    elif [ "$FH" == "RHEL" ]; then
      [ "$1" == "enable" ] && svcsts="on"
      [ "$1" == "disable" ] && svcsts="off"
      cmd="chkconfig $2 $svcsts"
    elif [ "$FH" == "Debian" ]; then
      [ "$1" == "on" ] && svcsts="enable"
      [ "$1" == "off" ] && svcsts="disable"
      cmd="update-rc.d $2 $svcsts"
    fi
    echo "$cmd"
}

get_full_confn() {
# get_full_confn(action pkgname OPTS)
    local x p O
    local confn="$(get_prm_value ${2}_confn)"
    if [ -n "$confn" ]; then
      p=$(dirname $confn)
    else
      p=
    fi
    if [ "$p" == "." -a ! -f "$confn" ]; then
      local confpaths="$(get_prm_value ${2}_FINDCFN)"
      if [ -n "$confpaths" -a -n "$confn" ]; then
        O="$IFS"
        IFS=; read p1 p2 p3 p4 p5<<<"$confpaths"
        IFS="$O"
        p=$confn
        confn=$(findpkg "$confn" "$p1" "$p2" "$p3" "$p4" "$p5")
        if [ -z "$confn" ]; then
          read x p2 p3 p4 p5<<<"$p1"
          confn=$x/$p
        fi
      fi
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "$confn=get_full_confn($1 $2 $3)">>~/$THIS.his
    echo "$confn"
}

get_dir_URL() {
# get_dir_URL(action pkgname OPTS [param] [SPECIMEN])
    local pkg lop lreqver rop rreqver branch x pkgURL subpkg
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    local xtlcmd=$(get_act_xtlcmd $2 "$1" $3 $4 $5)
    subpkg=$2
    if [[ $3 =~ s ]]; then
      pkg_URL="$(get_prm_value ${pkg}__${2}_${xtlcmd}_URL)"
      if [ -n "$pkg_URL" ]; then
        subpkg=$(basename $pkg_URL)
        if [ "${subpkg: -4}" == ".git" ]; then
          subpkg=${subpkg:0: -4}
        fi
      fi
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "$subpkg=get_dir_URL($1 $2 $3 $4 $5)">>~/$THIS.his
    echo "$subpkg"
}

get_full_URL() {
# get_full_URL(action pkgname OPTS [param] [SPECIMEN])
    local pkg lop lreqver rop rreqver branch x pkgURL
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    local xtlcmd=$(get_act_xtlcmd $2 "$1" $3 $4 $5)
    if [[ $3 =~ s ]]; then
      pkg_URL="$(get_prm_value ${pkg}__${2}_${xtlcmd}_URL)"
      if [ -z "$pkg_URL" ]; then
        pkg_URL="$(get_prm_value ${pkg}_${xtlcmd}_URL)"
        pkg_URL=${pkg_URL%/*}/${2}.git
      fi
    else
      pkg_URL="$(get_prm_value ${2}_${xtlcmd}_URL)"
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "$pkg_URL=get_full_URL($1 $2 $3 $4 $5)">>~/$THIS.his
    echo "$pkg_URL"
}

get_full_LocalRoot() {
# get_full_LocalRoot(action pkgname OPTS [param] [SPECIMEN])
    local pkg lop lreqver rop rreqver branch x
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    local xtlcmd=$(get_act_xtlcmd $2 "$1" $3 $4 $5)
    if [[ $3 =~ s ]]; then
      LocalRoot="$(get_prm_value ${pkg}_${xtlcmd}_LocalRoot)"
    else
      LocalRoot="$(get_prm_value ${2}_${xtlcmd}_LocalRoot)"
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "$LocalRoot=get_full_LocalRoot($1 $2 $3 $4 $5)">>~/$THIS.his
    echo "$LocalRoot"
}

do_git_localroot() {
# do_git_localroot(action pkgname OPTS [param] [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " "  "do_git_localroot($1 $2 $3 $4 $5)">>~/$THIS.his
    local pkgname="$2"
    local xtlcmd=git
    local pkg lop lreqver rop rreqver branch v LocalDir
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    local sts=$STS_FAILED
    if [[ $3 =~ s ]] && [ -n "$branch" ]; then
      local LocalRoot=$(get_full_LocalRoot "$1" ${pkg} $3 $4 "$5")
      local pkg_branch="$(get_prm_value ${pkg}_Branch)"
      local pLocalDir="$(get_prm_value ${pkg}_${xtlcmd}_LocalDir)"
      [ -n "$pLocalDir" ] && LocalDir=$(expand_param $pLocalDir $branch)
    else
      local LocalRoot=$(get_full_LocalRoot "$1" ${pkgname} $3 $4 "$5")
      local pkg_branch="$(get_prm_value ${pkgname}_Branch)"
      local pLocalDir="$(get_prm_value ${pkgname}_${xtlcmd}_LocalDir)"
      [ -n "$pLocalDir" ] && LocalDir=$(expand_param $pLocalDir $branch)
    fi
    if [ -n "$LocalRoot" ]; then
      local pgkpath=
      if [ "$1" == "install" ]; then
        if [ ! -d $LocalRoot ]; then
          mkdir_traced $LocalRoot
        fi
        if [[ $3 =~ s ]] && [ -n "$branch" ]; then
          pgkpath=$LocalRoot/$LocalDir
        else
          pgkpath=$LocalRoot
        fi
      fi
      if [ -d $LocalRoot -a "$1" != "install" ]; then
        if [ -n "$pkg_branch" ]; then
          if [ -n "$branch" -a "$1" != "version" ]; then
            pgkpath=$LocalRoot/$LocalDir
          else
            for v in $pkg_branch; do
              LocalDir=$v
              [ -n "$pLocalDir" ] && LocalDir=$(expand_param $pLocalDir $v)
              if [ -d $LocalRoot/$LocalDir ]; then
                if $(cmp_ver "$v" "$lop" "$lreqver") && $(cmp_ver "$v" "$rop" "$rreqver"); then
                  pgkpath=$LocalRoot/$LocalDir
                fi
              fi
            done
          fi
        else
          pgkpath=$LocalRoot
        fi
      fi
      if [ -n "$pgkpath" ]; then
        if [ "$1" == "install" ]; then
          run_traced "cd $pgkpath"
        else
          cd $pgkpath
        fi
        sts=$?
      fi
    else
      if [ -d /tmp/$pkgname ]; then
        run_traced "rm -fR /tmp/$pkgname"
      elif [ -f /tmp/$pkgname ]; then
        run_traced "rm -f /tmp/$pkgname"
      fi
      if [ "$1" == "install" ]; then
        run_traced "cd /tmp"
      else
        cd /tmp
      fi
      sts=$?
    fi
    return $sts
}


add_oca_dependencies() {
# add_oca_dependencies(action pks OPTS [param] [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " "  "oca_dependencies($1 $2 $3 $4 $5)">>~/$THIS.his
    local pkgs="$2"
    local xtlcmd=git
    local pkg lop lreqver rop rreqver branch LocalDir f n p v subpkg add_pkgs
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    local sts=$STS_FAILED
    local LocalRoot=$(get_full_LocalRoot "$1" ${pkg} $3 $4 "$5")
    local pLocalDir="$(get_prm_value ${pkg}_${xtlcmd}_LocalDir)"
    [ -n "$pLocalDir" ] && LocalDir=$(expand_param $pLocalDir $branch)
    add_pkgs=
    for subpkg in $pkgs; do
      if [ $test_mode -eq 0 -a -f $LocalRoot/$LocalDir/$subpkg/oca_dependencies.txt ]; then
        f=$LocalRoot/$LocalDir/$subpkg/oca_dependencies.txt
        while IFS=\= read p n v; do
          if [[ " $pkgs " =~ [[:space:]]$o[[:space:]] ]]; then
            :
          else
            add_pkgs="$add_pkgs $p"
          fi
        done < $f
      fi
    done
    echo $add_pkgs
}

update_hba_conf() {
    local fnd_mrk=0
    local eorem=0
    local patched=0
    local dt=$(date +%Y-%m-%d)
    while IFS=\~ read -r line; do
      if [ "${line:0:11}" == "# [PRODUCT:" ]; then
        fnd_mrk=1
      elif [[ $line =~ ^[a-zA-Z_].*$ ]]; then
        eorem=1
      fi
      if [ $eorem -eq 1 -a $patched -eq 0 -a $fnd_mrk -eq 0 ]; then
         patched=1
         echo "# [PRODUCT: $dt] Added access to Odoo">>$1.tmp
         echo "# Where user were created, no password was issued, so this configuration uses trust method to connect">>$1.tmp
         echo "# Warning! This method is unsecure for no local connection">>$1.tmp
         echo "# In order to set postgresql secure you must:">>$1.tmp
         echo "# 1. drop users automatically created; type >drop user odoo[7|8|9]">>$1.tmp
         echo "# 2. recreate users">>$1.tmp
         echo "#    type >createuser --createdb --username postgres --no-createrole --no-superuser --pwprompt odoo[7|8|9]">>$1.tmp
         echo "#    Rememeber password you type, because you must insert it in odoo conf file">>$1.tmp
         echo "# 3. Correct following lines, subst 'trust' by 'md5' for users odoo[7|8|9]">>$1.tmp
         echo "# 4. Restart service postgresql">>$1.tmp
         echo "# When you connect to psql console do not forget dbname, type:">>$1.tmp
         echo "# psql -Uodoo[7|8|9] postgres">>$1.tmp
         echo "# User openerp is used by old installation schema of Odoo7 (formerly OpenERP)">>$1.tmp
         echo "local   all         openerp                           peer">>$1.tmp
         echo "host    all         openerp     127.0.0.1/32          trust">>$1.tmp
         echo "host    all         openerp     ::1/128               trust">>$1.tmp
         echo "local   all         odoo                              peer">>$1.tmp
         echo "host    all         odoo        127.0.0.1/32          trust">>$1.tmp
         echo "host    all         odoo        ::1/128               trust">>$1.tmp
         for ov in 6 7 8 9 10; do
           echo "local   all         odoo$ov                             trust">>$1.tmp
           echo "host    all         odoo$ov       127.0.0.1/32          trust">>$1.tmp
           echo "host    all         odoo$ov       ::1/128               trust">>$1.tmp
         done
         echo "# [PRODUCT: end automatic configuration]">>$1.tmp
      fi
      echo "$line">>$1.tmp
    done < "$1"
    if [ $opt_dry_run -eq 0 ]; then
      [ -z "opt_yes" ] || vim $1.tmp
      run_traced "mv $1 $1.bak"
      run_traced "mv $1.tmp $1"
    else
      vim $1.tmp
    fi

}

build_groups_list() {
    infile=/etc/group
    outfile=~/z0_x_groups.txt
    if [ $opt_dry_run -eq 0 -a -f $outfile ]; then rm -f $outfile; fi
    while IFS=: read -r group enpass gid other; do
      if [ $gid -ge 400 ]; then
        valid=1
      else
        valid=0
      fi
      if [[ "$group" =~ kluser* ]]; then valid=0; fi
      if [[ "$group" =~ saslauth* ]]; then valid=0; fi
      if [[ "$group" =~ dev131* ]]; then valid=0; fi
      if [[ "$group" =~ cgred* ]]; then valid=0; fi
      if [[ "$group" =~ odoo* ]]; then valid=0; fi
      if [[ "$group" =~ openerp* ]]; then valid=0; fi
      if ((valid)); then
        wlog "# Group $group ($gid)"
        if [ $opt_dry_run -eq 0 ]; then
          echo "$group:$enpass:$gid">>$outfile
        fi
      fi
    done < "$infile"
}

build_user_list() {
    infile=/etc/passwd
    outfile=~/z0_x_users.txt
    if [ $opt_dry_run -eq 0 -a -f $outfile ]; then rm -f $outfile; fi
    while IFS=: read -r user enpass uid gid desc home shell; do
      if [ $uid -ge 400 ]; then
        valid=1
      else
        valid=0
      fi
      if [[ "$user" =~ kluser* ]]; then valid=0; fi
      if [[ "$user" =~ saslauth* ]]; then valid=0; fi
      if [[ "$user" =~ dev131* ]]; then valid=0; fi
      if ((valid)); then
        wlog "# User $user ($uid) assigned \"$home\" home directory with $shell shell."
        if [ $opt_dry_run -eq 0 ]; then
          echo "$user:$enpass:$uid:$gid:$desc:$home:$shell">>$outfile
        fi
      fi
    done < "$infile"
}

add_groups() {
    sysfile=/etc/group
    infile=~/z0_x_groups.txt
    if [ -f $infile ]; then
      while IFS=: read -r group enpass gid other; do
        if [ $(grep "^$group:" $sysfile 2>/dev/null) ]; then
          wlog "!! Group $group already exists"
        else
          cmd="groupadd -g $gid $group"
          wlog "$cmd"
          if [ $opt_dry_run -eq 0 ]; then
            $cmd
          fi
        fi
      done < "$infile"
    else
      wlog "!! File $infile not found!"
    fi
}

add_users() {
    sysfile=/etc/passwd
    infile=~/z0_x_users.txt
    if [ -f $infile ]; then
      while IFS=: read -r user enpass uid gid desc home shell; do
        if [ $(grep "^$user:" $sysfile 2>/dev/null) ]; then
          wlog "!! User $user already exists"
        else
          if [ ! -d $home ]; then cmd="useradd -m"; else cmd="useradd"; fi
          cmd="$cmd -u $uid -g $gid -d $home -s $shell $user"
          wlog "$cmd"
          if [ $opt_dry_run -eq 0 ]; then
            $cmd
            if [ "$opt_pwd" ]; then
              echo $opt_pwd|passwd --stdin $user
            fi
          fi
        fi
      done < "$infile"
    else
      wlog "!! File $infile not found!"
    fi
}

create_setup() {
    local pkgname=$1
    if [ -z "$2" ]; then
      local SETUP=./setup.sh
    else
      local SETUP=$2
    fi
    local BINLIST="$(get_prm_value ${pkgname}_binPkgList $pkgname)"
    local ETCLIST="$(get_prm_value ${pkgname}_etcPkgList $pkgname)"
    local RQ_Z0LIB="$(get_prm_value ${pkgname}_build_with_z0libr)"
    local RQ_OELIB="$(get_prm_value ${pkgname}_build_with_odoorc)"
    local datetime=$(date +"%Y-%m-%d %H:%M")
    if [ "$pkgname" == "lisa" ]; then
      if [ -n "$opt_dev" ]; then
        cp ../../z0lib/z0lib/z0librc ./
        cp ../../clodoo/clodoo/odoorc ./
      else
        cp /etc/z0librc ./
        cp /etc/odoorc ./
      fi
      cat <<EOF >$SETUP
# lisa setup $__version__ ($datetime)
THIS=\$(basename "\$0")
TDIR=\$(readlink -f \$(dirname \$0))
pkgname=$THIS
tarball=$pkgname.tar.gz
opt_verbose=0
opt_dry_run=0
if [ "\${1:0:1}" == "-" ]; then
  if [[ "\$1" =~ v ]]; then opt_verbose=1; fi
  if [[ "\$1" =~ n ]]; then opt_dry_run=1; fi
fi
if [ \${opt_dry_run:-0} -eq 0 ]; then
  pfx="\\\$"
else
  pfx=">"
fi
if [ -f \$TDIR/z0librc -o \$opt_dry_run -gt 0 ]; then
  [ \$opt_dry_run -gt 0 ] || . \$TDIR/z0librc
  if [ "\$TDIR" != "/usr/bin" ]; then
    if [ "\${TDIR:0:14}" == "/opt/odoo/dev/" ]; then
      cpmv="cp"
    else
      cpmv="mv"
    fi
    [ \$opt_verbose -gt 0 ] && echo "\$pfx mkdir -p /etc/$THIS"
    [ \$opt_dry_run -gt 0 ] || mkdir -p /etc/$THIS
    for f in $BINLIST; do
      if [ -f \$TDIR/\$f ]; then
        [ \$opt_verbose -gt 0 ] && echo "\$pfx \$cpmv \$TDIR/\$f /usr/bin"
        [ \$opt_dry_run -gt 0 ] || eval \$cpmv \$TDIR/\$f /usr/bin
        [ \$opt_verbose -gt 0 ] && echo "\$pfx [ -x /usr/bin/\$f ] && chmod +x /usr/bin/\$f"
        [ \$opt_dry_run -gt 0 ] || [ -x /usr/bin/\$f ] && chmod +x /usr/bin/\$f
      fi
    done
    for f in $ETCLIST; do
      if [ -f \$TDIR/\$f ]; then
        [ \$opt_verbose -gt 0 ] && echo "\$pfx \$cpmv \$TDIR/\$f /etc/$THIS"
        [ \$opt_dry_run -gt 0 ] || eval \$cpmv \$TDIR/\$f /etc/$THIS
      fi
    done
    [ \$opt_verbose -gt 0 ] && echo "\$pfx cd \$TDIR"
    [ \$opt_dry_run -gt 0 ] || cd \$TDIR
    [ \$opt_verbose -gt 0 -a $RQ_Z0LIB -gt 0 ] && echo "\$pfx _install_z0librc"
    [ \$opt_dry_run -eq 0 -a $RQ_Z0LIB -gt 0 ] && _install_z0librc
    [ \$opt_verbose -gt 0  -a $RQ_OELIB -gt 0 ] && echo "\$pfx \$cpmv \$TDIR/\odoorc /etc"
    [ \$opt_dry_run -eq 0 -a $RQ_OELIB -gt 0 ] && eval \$cpmv \$TDIR/\odoorc /etc
    [ \$opt_verbose -gt 0 ] && echo "\$pfx cd .."
    [ \$opt_dry_run -gt 0 ] || cd ..
    if [ -d "./\$pkgname" ]; then
      [ \$opt_verbose -gt 0 ] && echo "rm -fR ./\$pkgname"
      [ \$opt_dry_run -gt 0 ] || rm -fR ./\$pkgname
    fi
    if [ -f "./\$tarball" ]; then
      [ \$opt_verbose -gt 0 ] && echo "rm -f ./\$tarball"
      [ \$opt_dry_run -gt 0 ] || rm -f ./\$tarball
    fi
  fi
else
  echo "Library z0librc not found!"
  exit 1
fi
EOF
    fi
    chmod +x ./setup.sh
}

create_pkglist() {
# create_pkglist(pkgname pkgpath)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "create_pkglist($1 $2 $3)">>~/$THIS.his
    local pkgname=$1
    local fileignore="$(get_prm_value build_fileignore list)"
    local x xx PKGLIST BINLIST ETCLIST Z0LIB OELIB PKGPATH=$2
    [ -z "$PKGPATH" ] && PKGPATH=.
    PKGLIST="$(get_prm_value ${pkgname}_build_PKGLIST list)"
    x="$(get_prm_value ${pkgname}_build_with_z0libr)"
    if [ "$x" == "1" ]; then
      Z0LIB=$(findpkg z0librc "/etc . .. $TDIR $TDIR/.. $TDIR/../z0lib $TDIR/../../z0lib")
      [ -z "$Z0LIB" ] && Z0LIB=z0librc
    fi
    x="$(get_prm_value ${pkgname}_build_with_odoorc)"
    if [ "$x" == "1" ]; then
      OELIB=$(findpkg odoorc "$HOME/dev . .. $TDIR $TDIR/.. $TDIR/../clodoo $TDIR/../../clodoo")
      [ -z "$OELIB" ] && OELIB=odoorc
    fi
    BINLIST="$(get_prm_value ${pkgname}_binPkgList list)"
    ETCLIST="$(get_prm_value ${pkgname}_etcPkgList list)"
    if [ -n "$PKGLIST" ]; then
      PKGLIST=${PKGLIST//,/ }
    else
      pushd $PKGPATH > /dev/null
      xx="$(get_prm_value ${pkgname}_build_fileignore list)"
      fileignore="$fileignore $xx"
      x="find . -type f"
      for f in $fileignore "setup.*"; do
        if [ "${f: -1}" == "/" ]; then
          x="$x -not -path '*/$f*'"
        else
          x="$x -not -name '*$f'"
        fi
      done
      eval $x >./tmp.log
      PKGLIST="$(cat ./tmp.log)"
      rm -f ./tmp.log
      popd $PKGPATH > /dev/null
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " ">>> PKGLIST=$PKGLIST $BINLIST $ETCLIST $Z0LIB $OELIB">>~/$THIS.his
    echo "$PKGLIST $BINLIST $ETCLIST $Z0LIB $OELIB"
}

do_dry_build_package() {
#do_dry_build_package(action pkgname OPTS)
# Build a distribution package from current dir
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_build_package($1 $2 $3)">>~/$THIS.his
    local package="$2"
    local O s x PKGLIST
    # local tarball=$package.tar.gz
    local tarball=$(get_prm_value ${pkgname}_install_xname)
    local SETUP=./setup.sh
    PKGLIST=$(create_pkglist $1)
    s=0
    for f in $PKGLIST; do
      if [ ! -f $f ]; then
        s=1
        echo "!! Missed $f!"
      fi
    done
    if [ $s -eq 0 ]; then
      if [ -f /tmp/$package ]; then
        rm -f /tmp/$package
      elif [ -d /tmp/$package ]; then
        rm -fR /tmp/$package
      fi
      if [ -f /tmp/$tarball ]; then
        rm -f /tmp/$tarball
      fi
      run_traced "mkdir -p /tmp/$package"
      for f in $PKGLIST; do
        run_traced "cp $f /tmp/$package/"
      done
      create_setup $package $SETUP
      run_traced "cp $SETUP /tmp/$package/"
      O=$PWD
      run_traced "cd /tmp"
      run_traced "tar -cf $tarball ./$package"
      run_traced "cd $O"
      run_traced "mv /tmp/$tarball ../"
      run_traced "rm -fR /tmp/$package"
    fi
}

do_dry_build_lisa() {
# do_dry_build_lisa(action pkgname OPTS)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_build_lisa($1 $2 $3)">>~/$THIS.his
    local package="$2"
    local f s x
    local tarball=$package.tar.gz
    local BINLIST="$(get_prm_value ${pkgname}_binPkgList $package)"
    local ETCLIST="$(get_prm_value ${pkgname}_etcPkgList $package)"
    if [ -f /tmp/$package ]; then
      rm -f /tmp/$package
    elif [ -d /tmp/$package ]; then
      rm -fR /tmp/$package
    fi
    if [ -f /tmp/$tarball ]; then
      rm -f /tmp/$tarball
    fi
    run_traced "mkdir -p /tmp/$package"
    s=0
    for f in $BINLIST $ETCLIST; do
      if [ ! -f $TDIR/$f ]; then
        s=1
        break
      fi
    done
    if [ $s -eq 0 ]; then
      if [ -f $tarball ]; then
        rm -f $tarball
      fi
      create_setup lisa
      for f in setup.sh $BINLIST $ETCLIST; do
        if [ -f $TDIR/$f ]; then
          run_traced "cp $TDIR/$f /tmp/$package/"
        else
          echo "Missed $TDIR/$f"
        fi
      done
    else
      for f in $BINLIST; do
        if [ -f /opt/odoo/lisa/$f ]; then
          run_traced "cp /opt/odoo/lisa/$f /tmp/$package/"
        elif [ -f /usr/bin/$f ]; then
          run_traced "cp /usr/bin/$f /tmp/$package/"
        else
          echo "Missed /usr/bin/$f"
        fi
      done
      for f in $ETCLIST; do
        if [ -f /opt/odoo/lisa/$f ]; then
          run_traced "cp /opt/odoo/lisa/$f /tmp/$package/"
        elif [ -f /etc/$package/$f ]; then
          run_traced "cp /etc/$package/$f /tmp/$package/"
        else
          echo "Missed /etc/$package/$f"
        fi
      done
    fi
    s="$(get_prm_value ${pkgname}_build_with_z0libr)"
    if [ ${s:-0} -gt 0 ]; then
      run_traced "cp /etc/z0librc /tmp/$package/"
      if [ -L $TDIR/z0librc -o -f $TDIR/z0librc ]; then
        run_traced "rm -f $TDIR/z0librc"
      fi
    fi
    s="$(get_prm_value ${pkgname}_build_with_odoorc)"
    if [ ${s:-0} -gt 0 ]; then
      run_traced "cp ../../clodoo/clodoo/odoorc /tmp/$package/"
      if [ -L $TDIR/odoorc -o -f $TDIR/odoorc ]; then
        run_traced "rm -f $TDIR/odoorc"
      fi
    fi
    run_traced "chown odoo:odoo /tmp/$package/*"
    run_traced "cd /tmp"
    run_traced "tar -cf $tarball ./$package"
    if [ -d /var/www/html/mw/download/ ]; then
      run_traced "chown apache:apache $tarball"
      run_traced "mv $tarball /var/www/html/mw/download/"
      run_traced "rm -fR ./$package"
    fi
    return $STS_SUCCESS
}

end_remove() {
# end_remove(action OPTS)
    local cmd=
    if [ "$FH" == "Debian" ]; then
      cmd="apt-get autoremove"
      if [[ $2 =~ J ]]; then
        cmd="# $cmd"
      fi
    fi
    if [ -n "$cmd" ]; then
      run_traced "$cmd"
    fi
    return $STS_SUCCESS
}

get_ver_num() {
    local xtlver=$(echo "$1"|grep -Eo '[0-9]+\.[0-9]+(\.[0-9]+|)'|awk -F. '{print $1*10000 + $2*100 + $3}')
    if [ -z "$xtlver" ]; then xtlver="0"; fi
    echo $xtlver
}

cmp_ver() {
# cmp_ver(cur_ver op req_ver)
    local curver=$(get_ver_num $1)
    local reqver=$(get_ver_num $3)
    if [ -z "$curver" ]; then
      curver=$reqver
    fi
    local sts=1
    if [ -z "$2" ]; then
      sts=0
    elif [ "$2" == "==" ]; then
      if [ $curver -eq $reqver ]; then sts=0; fi
    elif [ "$2" == ">=" ]; then
      if [ $curver -ge $reqver ]; then sts=0; fi
    elif [ "$2" == "<" ]; then
      if [ $curver -lt $reqver ]; then sts=0; fi
    elif [ "$2" == "<=" ]; then
      if [ $curver -le $reqver ]; then sts=0; fi
    elif [ "$2" == ">" ]; then
      if [ $curver -gt $reqver ]; then sts=0; fi
    elif [ "$2" == "!=" ]; then
      if [ $curver -ne $reqver ]; then sts=0; fi
    elif [ "$2" == "<>" ]; then
      if [ $curver -ne $reqver ]; then sts=0; fi
    fi
    return $sts
}

satisfy_request() {
# satisfy_request(action pkgname OPTS [param] [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "satisfy_request($1 $2 $3)">>~/$THIS.his
    local sts=127
    local s
    local pkgname=$2
    local ReqPkgList="$(get_prm_value ${pkgname}_ReqPkgList list)"
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "    ${pkgname}_ReqPkgList=$ReqPkgList">>~/$THIS.his
    local xtlcmd=$(get_act_xtlcmd $2 install $3 $4 "")
    if [ "$xtlcmd" == "git" -o "$xtlcmd" == "wget" ]; then
      if [[ "$ReqPkgList" =~ $xtlcmd ]]; then
        :
      else
        local ReqPkgList="$ReqPkgList $xtlcmd"
      fi
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  REQ=$ReqPkgList">>~/$THIS.his
    if [ -n "$ReqPkgList" -a "$1" != "whatis" -a "$1" != "version" ]; then
      [ $(is_virtualname $2) ] || ((prdstk_level++))
      if [[ $3 =~ b ]]; then local x="-qb"; else local x="-q"; fi
      for p in $ReqPkgList; do
        realname=$(get_realname $p $1 $3)
        if $(please status $realname $x "$4" ""); then
          [ $sts -eq 127 ] && sts=$STS_SUCCESS
        else
          please install $realname $3 "$4" ""
          s=$?
          [ $sts -eq 127 ] && sts=$s
          [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
        fi
      done
      [ $(is_virtualname $2) ] || ((prdstk_level--))
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #satisfy_request">>~/$THIS.his
    return $sts
}

do_action_subpkg() {
# do_action_subpkg(pfx action pkgname OPTS [param] [SPECIMEN])
    [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_action_subpkg($1 $2 $3 $4 $5 $6)">>~/$THIS.his
    local p
    local x
    local rs=$(get_realname $3 $2 $4)
    local realname
    local opts=$4
    if [[ $4 =~ [qQ] ]]; then
      :
    else
      local iter=$(get_iter $3)
      [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " ">>> iter=$iter">>~/$THIS.his
      local sts=$STS_SUCCESS
      if [ "$3" != "$iter" ]; then
        for p in $iter; do
          if [ "$p" != "$3" -a "$p" != "$rs" ]; then
            # realname=$(get_realname $p $2 $4)
            # do_action_subpkg "$1" $2 $realname ${opts}Q
            do_action_subpkg "$1" $2 $p ${opts}Q
            sts=$?
            if [ $sts -ne $STS_SUCCESS ]; then
              break
            fi
          fi
        done
      else
        do_action_subpkg "$1" $2 $3 ${opts}Q $5 "$6"
        sts=$?
      fi
      [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #do_action_subpkg">>~/$THIS.his
      return $sts
    fi
    if [ "$2" != "whatis" -a "$2" != "version" ]; then
      verbose_msg "Analyzing $3"
    fi
    opts=${opts//Q/}
    opts=${opts//q/}
    please $1$2 $3 $opts "$4" "$5" "$6"
    sts=$?
    [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #do_action_subpkg">>~/$THIS.his
    return $sts
}


do_act_all_deppkgs() {
# do_act_all_deppkgs(pfx action pkgname OPTS [param] [SPECIMEN])
    [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_act_all_deppkgs($1 $2 $3 $4)">>~/$THIS.his
    local sts=$STS_SUCCESS
    local p s
    local BOPlist="$(get_prm_value ${3}_BOP list)"
    if [ -n "$BOPlist" -a $prdstk_level -lt $opt_depth ]; then
      [ $(is_virtualname $3) ] || ((prdstk_level++))
      [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " ">>> BOPlist=$BOPlist">>~/$THIS.his
      for p in $BOPlist; do
        do_action_subpkg "$1" $2 $p $4 $5 "$6"
        s=$?
        [ $sts -eq 127 ] && sts=$s
        [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
      done
      [ $(is_virtualname $3) ] || ((prdstk_level--))
    fi
    [[ $4 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #do_act_all_deppkgs">>~/$THIS.his
    return $sts
}

lisa_update_odoo() {
# lisa_install_odoo
    # init_odoo_env
    if [ "$opt_odoo" == "*" -o -z "$opt_odoo" ]; then
      local ov_iter="10 9 8 7 6"
    else
      local ov_iter=$opt_odoo
    fi
    for ov in $ov_iter; do
      local ovf=$(get_odoo_full_ver $ov)
      local xtl=0
      if [ -d /opt/odoo/$ovf ]; then
        run_traced "cd /opt/odoo/$ovf"
        if [ $test_mode -gt 0 ]; then
          :
        else
          git status &>/dev/null
        fi
        if [ $? -ne 128 ]; then xtl=1; fi
        if [ $xtl -gt 0 ]; then
          run_traced "git pull origin $ovf" "odoo"
          if [ $test_mode -eq 0 ]; then
             opt_sub=$(grep "\[submodule" .gitmodules|awk '{print $2}'|tr -d "\"]"|tr "\n" " ")
          fi
          if [ -n "$opt_osub" ]; then
            lisa_install_submodule_by_git "$opt_osub" "$ov"
          fi
          if [ -n "$opt_sub" ]; then
            lisa_install_submodule_by_git "$opt_sub" "$ov"
          fi
          if [ -n "$opt_locale" ]; then
            lisa_install_submodule_by_git "$opt_locale" "$ov"
          fi
        fi
        run_traced "git checkout $ovf"
      fi
    done
}

lisa_config_odoo() {
    if [ "$opt_odoo" == "*" -o -z "$opt_odoo" ]; then
      local ov_iter="10 9 8 7 6"
      local opt_multi="-m"
    else
      local ov_iter=$opt_odoo
      if [ $opt_mult -gt 0 ]; then
        local opt_multi="-m"
      else
        local opt_multi=""
      fi
    fi
    local userhome=$(sudo -u postgres -i eval 'echo "$HOME"')
    if [ -z "$userhome" ]; then
      userhome=$(grep postgres /etc/passwd|awk -F":" '{print $6}')
    fi
    if [ -f $userhome/.pgpass ]; then
      local pwd=$(grep odoo $userhome/.pgpass|awk -F: '{ print $5 }'|head -n1)
    else
      local pwd=
      while [ -z "$pwd" ]; do
        read -rsp"Type password for postgresql user access (no echo)>" pwd
        echo -e "\n"
        read -rsp"Retype password for validation>" pwd1
        echo -e "\n"
        if [ "$pwd" != "$pwd1" ]; then
          local pwd=
        fi
      done
    fi
    echo "*:*:*:odoo:$pwd">$userhome/.pgpass
    chown postgres:postgres $userhome/.pgpass
    chmod u=rw,g=,o= $userhome/.pgpass
    f_hba=$(findpkg "pg_hba.conf" "/var/lib /etc" "postgresql pgsql" "9.4 9.3 9.2 9.1 9.0 8.4" "data main")
    update_hba_conf $f_hba
    if [ -n "$f_hba" ]; then
      vi $f_hba
    fi
    for ov in $ov_iter; do
      local ovf=$(get_odoo_full_ver $ov)
      if [ -n "$opt_multi" ]; then
        uu="odoo$ov"
        echo "*:*:*:$uu:$pwd">>$userhome/.pgpass
      elif [ "$ov" == "7" ]; then
        uu=openerp
      else
        uu=odoo
      fi
      local x=$(sudo -iupostgres psql -c \\dg|grep " $uu ")
      if [ -z "$x" ]; then
        elog "Create account $uu for postgres access by odoo"
        echo "Warning: store password you type in odoo config file"
        run_traced "sudo -iupostgres createuser --createdb --username postgres --no-createrole --no-superuser --no-password $uu"
      fi
      elog "Running odoo $ov to configure"
      if [ -f /opt/odoo/.openerp_serverrc ]; then
        rm -f /opt/odoo/.openerp_serverrc
      fi
      cmd="sudo -iuodoo /opt/odoo/$ovf/openerp-server"
      cmd="$cmd --addons-path=/opt/odoo/$ovf/openerp/addons,/opt/odoo/$ovf/addons"
      for iso in ${opt_osub//,/ } ${opt_sub//,/ }; do
        # pn=$(get_locale_name "$iso")
        if [ -z "$pn" ]; then
          elog  "Invalid submodule name $iso"
        else
          # addons=$(get_locale_dir "$pn")
          if [ -d $addons ]; then
            cmd="$cmd,/opt/odoo/$ovf/$addons"
          fi
        fi
      done
      for iso in ${opt_locale//,/ }; do
        # pn=$(get_locale_name "$iso")
        if [ -z "$pn" ]; then
          elog  "Invalid $iso in -l $opt_locale switch"
        else
          # addons=$(get_locale_dir "$pn")
          if [ -d $addons ]; then
            cmd="$cmd,/opt/odoo/$ovf/$addons"
          fi
        fi
      done
      local xtl_pidfile=/var/run/odoo/$uu.pid
      local xtl_logfile=/var/log/odoo/${uu}-server.log
      cmd="$cmd -s --stop-after-init"
      cmd="$cmd --pidfile=$xtl_pidfile"
      cmd="$cmd --logfile=$xtl_logfile"
      cmd="$cmd --db_host=localhost"
      cmd="$cmd --db_user=$uu"
      cmd="$cmd --db_password=$pwd"
      if [ "$opt_multi" == "-m" ]; then
        cmd="$cmd --xmlrpc-port=816$ov"
      fi
      run_traced "$cmd"
      if [ -f /opt/odoo/.openerp_serverrc ]; then
        if [ $opt_dry_run -eq 0 ]; then
          sed -i "s:^data_dir *=.*Odoo:&$ov:" /opt/odoo/.openerp_serverrc
          [ -z "opt_yes" ] || vim /opt/odoo/.openerp_serverrc
        fi
        run_traced "cp /opt/odoo/.openerp_serverrc /etc/odoo/${uu}-server.conf"
        run_traced "chown odoo:odoo /etc/odoo/${uu}-server.conf"
        run_traced "chmod u+rw,g+r,o+r /etc/odoo/${uu}-server.conf"
        run_traced "$TDIR/lisa_bld_ods -E$FH -L$xtl_logfile -P$xtl_pidfile $opt_multi -O$ov ${uu}-server"
        run_traced "chown odoo:odoo ${uu}-server"
        run_traced "mv ${uu}-server /etc/init.d/"
      else
        elog "!Odoo error: configuration file not created!!"
      fi
    done
}

post_config_mysql-server() {
    if [ "$FH" == "RHEL" ]; then
      run_traced "mysql_secure_installation"
      run_traced "chkconfig mysqld on"
    else
      for cmd in mysql_install_db mysql_secure_installation; do
        run_traced "$cmd"
      done
      run_traced "chkconfig mysql on"
    fi
}

post_config_mariadb-server() {
    if [ "$FH" == "RHEL" ]; then
      run_traced "mysql_secure_installation"
    fi
    run_traced "chkconfig mariadb on"
}

lisa_install_submodule_by_git() {
# lisa_install_submodule_by_git(pkgs branch action OPTS param [SPECIMEN])
    local opt_osub="$1"
    local ov="$2"
    local ovf=$(get_odoo_full_ver $ov)
    CWD=$PWD
    for iso in ${opt_osub//,/ }; do
      run_traced "cd /opt/odoo/$ovf"
      # pn=$(get_locale_name "$iso")
      if [ -z "$pn" ]; then
        if [ ${#iso} -ne 2 ]; then
          elog  "Invalid submodule name $iso"
        else
          elog  "Invalid $iso in -l $opt_locale switch"
        fi
      else
        # addons=$(get_locale_dir "$pn")
        if [ -f .gitignore -o $test_mode -gt 0 ]; then
          if [ -z "$(grep "$addons/" .gitignore 2>/dev/null)" -o $test_mode -gt 0 ]; then
            run_traced "echo \"$addons/\">>.gitignore"
          fi
        fi
        if [ -d $addons -a $test_mode -eq 0 ]; then
          run_traced "cd $addons"
          run_traced "git pull origin $ovf" "odoo"
          run_traced "cd /opt/odoo/$ovf"
        else
          run_traced "git clone -b $ovf $pn $addons $git_opts" "odoo"
          run_traced "git submodule add -b $ovf -f $pn $addons" "odoo"
          if [ "opt_oed" == "zeroincombenze" ]; then
            run_traced "git remote add upstream https://github.com/OCA/$addons.git" "odoo"
          fi
          run_traced "cd /opt/odoo/$ovf"
        fi
      fi
    done
    cd $CWD
}

do_dry_install_submodule_by_git() {
# do_dry_install_submodule_by_git(action pks OPTS param [SPECIMEN])
# specimen contains top module name and version
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_install_submodule_by_git($1 \"$2\" $3 $4 \"$5\")">>~/$THIS.his
    local sts=$STS_SUCCESS
    local s
    local xtlcmd pkg_user pkg lop lreqver rop rreqver branch x p
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    if [ "$2" == "." ]; then
      xtlcmd=git
      pkg_user=$(get_prm_value ${pkg}_${xtlcmd}_LocalUser)
      pkg_group=$(echo $pkg_user:$pkg_user|awk -F: '{print $2}')
      pkg_user=$(echo $pkg_user|awk -F: '{print $1}')
      if [ "$pkg_user" == "$USER" ]; then
        pkg_user=
        pkg_group=
      fi
      run_traced "git submodule update --init" "$pkg_user"
    else
      CWD=$PWD
      if [ -n "$branch" ]; then
        run_traced "git checkout $branch"
      fi
      for pkgname in ${2//,/ }; do
        cd $CWD
        # do_1_status_package $1 $pkgname ${3}s $4 "$5"
        # sts=$?
        do_dry_install_by_git $1 $pkgname ${3}s $4 "$5"
        s=$?
        if [ $s -ne $STS_SUCCESS ]; then
          sts=$s
          break
        fi
      done
      cd $CWD
    fi
    return $sts
}


do_dry_install_by_git() {
# do_dry_install_by_git(action pkgname OPTS param [SPECIMEN])
# if install submodule (OPTS =~ s) specimen contains top module name and version
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_install_by_git($1 $2 $3 $4 \"$5\")">>~/$THIS.his
    local opt_dev
    [[ $3 =~ D ]] && opt_dev="-D"
    local pkgname=$2
    local xtlcmd=git
    local f s sts pkg_subm subpkg pkg_URL git_opts pkg_user pkg_branch pkg lop lreqver rop rreqver branch n x p LocalDir add_subpkgs
    local pLocalDir="$(get_prm_value ${pkgname}_${xtlcmd}_LocalDir)"
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    [ -n "$pLocalDir" ] && LocalDir=$(expand_param $pLocalDir $branch)
    if [[ $3 =~ s ]]; then
      subpkg=$2
      pkgname=${pkg}__$2
      pkg_URL="$(get_prm_value ${pkgname}_${xtlcmd}_URL)"
      if [ -z "$pkg_URL" ]; then
         pkg_URL="$(get_prm_value ${pkg}_${xtlcmd}_URL)"
         pkg_URL=${pkg_URL%/*}/$subpkg.git
      else
        subpkg=$(basename $pkg_URL)
        if [ "${subpkg: -4}" == ".git" ]; then
          subpkg=${subpkg:0: -4}
        fi
      fi
    else
      subpkg=
      pkg_URL="$(get_prm_value ${pkgname}_${xtlcmd}_URL)"
    fi
    if [ -z "$pkg_URL" ]; then
      elog "*** Package $pkgname not found for this platform"
      elog "    git $pkg_name from URL=$pkg_URL"
      return 2
    fi
    do_git_localroot $1 $2 $3 $4 "$5"
    if [ $? -eq $STS_SUCCESS ]; then
      git_opts=""
      if [ -n "$opt_dev" ]; then
        git_opts="--single-branch --depth=1 $git_opts"
      fi
      pkg_user=$(get_prm_value ${pkgname}_${xtlcmd}_LocalUser)
      pkg_group=$(echo $pkg_user:$pkg_user|awk -F: '{print $2}')
      pkg_user=$(echo $pkg_user|awk -F: '{print $1}')
      if [ "$pkg_user" == "$USER" ]; then
        pkg_user=
        pkg_group=
      fi
      if [ -z "subpkg" ]; then
        pkg_branch="$(get_prm_value ${pkgname}_Branch)"
      else
        pkg_branch="$(get_prm_value ${pkg}_Branch)"
      fi
      if [ -n "$pkg_branch" ]; then
        git_opts="-b $branch $git_opts"
      else
        branch=
      fi
      if [ -z "$subpkg" ]; then
        run_traced "git clone $pkg_URL $LocalDir $git_opts" "$pkg_user"
        sts=$?
      else
        if [ -f .gitignore -o $test_mode -gt 0 ]; then
          if [ -z "$(grep "$subpkg/" .gitignore 2>/dev/null)" -o $test_mode -gt 0 ]; then
            run_traced "echo \"$subpkg/\">>.gitignore"
          fi
        fi
        if [ -d $subpkg -a $test_mode -eq 0 ]; then
          run_traced "cd $subpkg"
          run_traced "git pull origin $branch" "$pkg_user"
          sts=$?
        else
          run_traced "git clone $pkg_URL $subpkg/ $git_opts" "$pkg_user"
          sts=$?
          run_traced "git submodule add -f $pkg_URL $subpkg/" "$pkg_user"
          x=$(get_prm_value "$pkgname_upstream")
          if [ -n "$x" ]; then
            run_traced "git remote add upstream $x" "$pkg_user"
          fi
        fi
      fi
      if [ -n "$pkg_user" ]; then
        run_traced "chown -R $pkg_user:$pkg_group $pkgname/"
      fi
      x=$(get_prm_value "${pkgname}_confdirs")
      if [ -n "$x" ]; then
        for p in ${x//,/ }; do
          if [ ! -d $p -o $test_mode -gt 0 ]; then
            if [ -n "$pkg_user" ]; then
              mkdir_traced $p $pkg_user:$pkg_group
            else
              mkdir_traced $p
            fi
          fi
        done
      fi
      if [ $sts -eq $STS_SUCCESS ]; then
        if [ "$pkgname" == "odoo" -a "$opt_oed" == "odoo"  ]; then
          pkg_subm=
        else
          pkg_subm="$(get_prm_value ${pkgname}_SubPkgList)"
        fi
        if [ -n "$pkg_subm" -a $opt_nake -eq 0 ]; then
          if [ "$pkgname" == "odoo" ]; then
            if [ "$opt_oed" == "zeroincombenze" ]; then
              pkg_subm="$pkg_subm l10n-italy-supplemental account_banking_cscs"
            fi
          fi
          do_dry_install_submodule_by_git $1 "$pkg_subm" $3 $4 "$5"
          s=$?
          [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
        fi
      fi
      if [ $sts -eq $STS_SUCCESS ]; then
        if [ -z "$subpkg" -a -n "$opt_locale" -a $opt_nake -eq 0 ]; then
          do_dry_install_submodule_by_git $1 "$opt_locale" $3 $4 "$5"
          s=$?
          [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
        fi
      fi
      if [ $sts -eq $STS_SUCCESS ]; then
        if [ -n "$opt_sub" -a $opt_nake -eq 0 ]; then
          do_dry_install_submodule_by_git $1 "$opt_sub" $3 $4 "$5"
          s=$?
          [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
        fi
      fi
      if [ $sts -eq $STS_SUCCESS -a "$pkgname" == "odoo" -a "$opt_oed" != "odoo" ]; then
        add_subpkgs=$(add_oca_dependencies $1 "$pkg_subm $subpkg $opt_sub" $3 $4 "$5")
        if [  -n "$add_subpkgs" ]; then
          do_dry_install_submodule_by_git $1 "$add_subpkgs" $3 $4 "$5"
          s=$?
          [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
        fi
      fi
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return #do_dry_install_by_git">>~/$THIS.his
    return $sts
}

do_dry_update_package() {
# do_dry_update_package(action pkgname OPTS [param] [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_update_package($1 $2 $3)">>~/$THIS.his
    local cmd0
    cmd=$(get_full_xtlcmd $1 $2 $3 $4 "$5")
    if [ ${UPDATED:-0} -eq 0 ] && [[  $cmd =~ apt-get.*upgrade ]]; then
      if [[ $3 =~ y ]]; then cmd0="apt-get -y update"; else cmd0="apt-get update"; fi
      run_traced "$cmd0"
      UPDATED=1
    fi
    run_traced "$cmd"
    local sts=$?
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #do_dry_update_package">>~/$THIS.his
    return $sts
}

do_dry_remove_lisa() {
# do_dry_remove_lisa(action pkgname OPTS)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_remove_lisa($1 $2 $3)">>~/$THIS
    run_traced "rm /usr/bin/$2"
    run_traced "rm /usr/bin/${2}.*"
    run_traced "rm /usr/bin/${2}_*"
    return $STS_SUCCESS
}

do_dry_update_by_wget() {
# do_update_by_wget(action pkgname OPTS)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_update_by_wget($1 $2 $3)">>~/$THIS.his
    local pkgname=$2
    local xtlcmd=wget
    local LocalRoot="$(get_prm_value ${pkgname}_${xtlcmd}_LocalRoot)"
    local LocalTmp="$(get_prm_value ${pkgname}_${xtlcmd}_LocalTmp)"
    local pkg_URL=$(get_prm_value ${pkgname}_${xtlcmd}_URL)
    local tarball=$(get_prm_value ${pkgname}_${xtlcmd}_xname)
    local pkg_md5="$(get_prm_value ${pkgname}_${xtlcmd}_md5)"
    if [ -z "$tarball" -o -z "$pkg_URL" ]; then
      elog "*** Package $pkgname not found for this OS version"
      elog "    wget $tarball from URL=$pkg_URL"
      return 2
    fi
    if [ -n "$LocalRoot" ]; then
      if [ ! -d $LocalRoot ]; then
        mkdir_traced $LocalRoot
      fi
      run_traced "cd $LocalRoot"
    else
      if [ -z "$LocalTmp" ]; then
        LocalTmp=/tmp/$pkgname
      fi
      run_traced "cd /tmp"
    fi
    if [ -f "./$tarball" ]; then
      run_traced "rm -f ./$tarball"
    fi
    run_traced "wget $pkg_URL/$tarball$pkg_md5"
    do_deploy_package $1 $pkgname "$3" $tarball "$LocalTmp"
    run_traced "rm -f $tarball"
    return $?
}

do_whatis_ports() {
# do_whatis_ports(action pkgname OPTS)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_whatis_ports($1 $2 $3)">>~/$THIS.his
    local lm X x=$((prdstk_level*2+1))
    [ $x -eq 0 ]||eval printf -v lm '%.s-' {1..$x}
    X="$(get_prm_value TCP_$2)"
    X=${X//,/ }
    if [ -n "$X" ]; then
      for port in $X; do
        test_msg "$lm TCP port $port"
      done
    fi
    X="$(get_prm_value UDP_$pkgname)"
    if [ -n "$X" ]; then
      for port in $X; do
        test_msg "$lm UDP port $port"
      done
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return(0) #do_whatis_ports">>~/$THIS.his
    return $STS_SUCCESS
}

post_install_ports() {
# post_install_ports(action pkgname OPTS)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "post_install_ports($1 $2 $3)">>~/$THIS.his
    local X="$(get_prm_value TCP_$2)"
    X=${X//,/ }
    if [ -n "$X" ]; then
      if [ "$1" == "install" -o "$1" == "update" -o "$1" == "config" ]; then
        for port in $X; do
          enable_port $port tcp $3
        done
      elif [ "$1" == "remove" ]; then
        for port in $X; do
          disable_port $port tcp $3
        done
      elif [ "$1" == "whatis" ]; then
        for port in $X; do
          test_msg "- TCP port $port"
        done
      fi
    fi
    local X="$(get_prm_value UDP_$pkgname)"
    if [ -n "$X" ]; then
      if [ "$1" == "install" -o "$1" == "update" -o "$1" == "config" ]; then
        for port in $X; do
          enable_port $port udp
        done
      elif [ "$1" == "remove" ]; then
        for port in $X; do
          disable_port $port tcp
        done
      elif [ "$1" == "whatis" ]; then
        for port in $X; do
          test_msg "- UDP port $port"
        done
      fi
    fi
    return $STS_SUCCESS
}

post_update_ports() {
# post_update_ports(action pkgname OPTS)
    if [[ $3 =~ b ]]; then printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "post_update_ports($1 $2 $3)">>~/$THIS.his; fi #debug
    local X="$(get_prm_value TCP_$2)"
    X=${X//,/ }
    if [ -n "$X" ]; then
      for port in $X; do
        enable_port $port tcp $3
      done
    fi
    local X="$(get_prm_value UDP_$pkgname)"
    if [ -n "$X" ]; then
      for port in $X; do
        enable_port $port udp $3
      done
    fi
    return $STS_SUCCESS
}


post_remove_ports() {
# post_remove_ports(action pkgname OPTS)
    if [[ $3 =~ b ]]; then printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "post_remove_ports($1 $2 $3)">>~/$THIS.his; fi #debug
    local X="$(get_prm_value TCP_$2)"
    X=${X//,/ }
    if [ -n "$X" ]; then
      for port in $X; do
        disable_port $port tcp $3
      done
    fi
    local X="$(get_prm_value UDP_$pkgname)"
    if [ -n "$X" ]; then
      for port in $X; do
        disable_port $port tcp $3
      done
    fi
    return $STS_SUCCESS
}

post_dry_update_service() {
# post_dry_update_service(action pkgname OPTS)
    if [[ $3 =~ b ]]; then printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "post_dry_update_service($1 $2 $3)">>~/$THIS.his; fi #debug
    local svcname=$(get_prm_value "${2}_svcname")
    local cmd=$(get_full_svccmd restart $svcname $3)
    run_traced "$cmd"
    local sts=$?
    return $sts
}

pre_dry_install_LAMP() {
# pre_dry_install_LAMP(action pkgname OPTS)
    if [[ $3 =~ D ]]; then
      if [ "$FH" == "RHEL" ]; then
        run_traced "yum $opt_yes groupinstall \"Development tools\""
      fi
    fi
    return $STS_SUCCESS
}

pre_dry_install_odoo() {
    if [ "$FH" == "RHEL" ]; then
      if [ ! -f /etc/yum.repos.d/odoo.repo ]; then
        run_traced "yum-config-manager --add-repo=https://nightly.odoo.com/8.0/nightly/rpm/odoo.repo"
      fi
    elif [ "$FH" == "Debian" ]; then
      if [ -z "$(apt-key list|grep info@odoo.com 2>/dev/null)" ]; then
        run_traced "wget -O - https://nightly.odoo.com/odoo.key | apt-key add -"
      fi
      if [ -z "$(cat /etc/apt/sources.list|grep 'nightly.odoo.com' 2>/dev/null)" ]; then
       run_traced "echo 'deb http://nightly.odoo.com/8.0/nightly/deb/ ./' >> /etc/apt/sources.list"
      fi
    fi
    pre_dry_config_odoo
}

pre_dry_update_LAMP() {
    pre_dry_install_LAMP
}

pre_dry_remove_service() {
# pre_dry_remove_service(action pkgname OPTS)
    if [[ $3 =~ b ]]; then printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "pre_dry_remove_service($1 $2 $3)">>~/$THIS.his; fi #debug
    local svcname=$(get_prm_value "${2}_svcname")
    local cmd=$(get_full_svccmd stop $svcname $3)
    run_traced "$cmd"
    local sts=$?
    return $sts
}

pre_dry_config_odoo() {
    ODOO_UID=$(get_prm_value "ODOO_UID")
    ODOO_GID=$(get_prm_value "ODOO_GID")
    sysfile=/etc/group
    group=odoo
    if [ "$ODOO_GID" ]; then
      x=$(grep "^[^:]*:[^:]:$ODOO_GID[^0-9]" $sysfile 2>/dev/null)
      if [ "$x" ]; then
        g=$(echo "$x"|awk -F: '{ print $1 }')
        if [ "$g" != "$group" ]; then
          wlog "# Required gid $ODOO_GID already assigned to $g"
        fi
      fi
    fi
    if [ -z $(grep "^$group:" $sysfile 2>/dev/null) ]; then
      if [ "$ODOO_GID" ]; then
        run_traced "groupadd -g $ODOO_GID $group"
      else
        run_traced "groupadd $group"
      fi
    fi
    if [ "$ODOO_GID" ]; then
      x=$(grep "^$group:.*" $sysfile 2>/dev/null)
      if [ "$x" ]; then
        gid=$(echo "$x"|awk -F: '{ print $3 }')
        if [ "$gid" != "$ODOO_GID" ]; then
          run_traced "groupmod -g $ODOO_GID $group"
        fi
      fi
    fi
    sysfile=/etc/passwd
    user=odoo
    if [ "$ODOO_UID" ]; then
      x=$(grep "^[^:]*:[^:]:$ODOO_UID[^0-9]" $sysfile 2>/dev/null)
      if [ "$x" ]; then
        u=$(echo "$x"|awk -F: '{ print $1 }')
        if [ "$u" != "$user" ]; then
          wlog "# Required uid $ODOO_UID already assigned to $u"
        fi
      fi
    fi
    if [ -z $(grep "^$user:" $sysfile 2>/dev/null) ]; then
      if [ ! -d /opt/odoo ]; then cmd="useradd -m"; else cmd="useradd"; fi
      if [ "$ODOO_UID" ]; then
        run_traced "$cmd -u $ODOO_UID -g odoo -d /opt/odoo -s /bin/bash $user"
      else
        run_traced "$cmd -r -g odoo -d /opt/odoo -s /bin/bash $user"
      fi
    fi
    if [ "$ODOO_UID" ]; then
      x=$(grep "^$user:.*" $sysfile 2>/dev/null)
      if [ "$x" ]; then
        uid=$(echo "$x"|awk -F: '{ print $3 }')
        if [ "$uid" != "$ODOO_UID" ]; then
          run_traced "usermod -u $ODOO_UID $user"
        fi
      fi
    fi
}

post_dry_install_odoo() {
    local p
    p=etc
    run_traced "mkdir -p /$p/odoo"
    run_traced "chown odoo:odoo /$p/odoo"
    run_traced "chmod -R u+rw,g+r,o+r /$p/odoo"
    for p in log lib run; do
      run_traced "mkdir -p /var/$p/odoo"
      run_traced "chown odoo:odoo /var/$p/odoo"
      run_traced "chmod -R u+rw,g+r,o+r /var/$p/odoo"
    done
}

post_dry_update_odoo() {
    post_dry_install_odoo
}

do_dry_status_package() {
# do_dry_status_package(action pkgname OPTS [param] [SPECIMEN])
    if [[ $3 =~ b ]]; then printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_status_package($1 $2 $3 $4 $5)">>~/$THIS.his; fi    #debug
    local lm x=$((prdstk_level*2))
    [ $x -eq 0 ]||eval printf -v lm '%.s-' {1..$x}
    do_1_status_package "$@"
    sts=$?
    local xtlcmd=$(get_act_xtlcmd $2 install $3 $4 "$5")
    if [ $sts -eq $STS_SUCCESS ]; then
      local cmd="wlog \"$lm$pkgname: installed by $xtlcmd\""
    else
      local cmd="wlog \"$lm$pkgname could be installed by $xtlcmd\""
    fi
    eval $cmd
    if [[ $3 =~ b ]]; then printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return(0) #dry_status_package">>~/$THIS.his; fi    #debug
    return $STS_SUCCESS
}

do_dry_version_package() {
# do_dry_version_package(action pkgname OPTS [param] [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_version_package($1 $2 $3 $4 $5)">>~/$THIS.his
    local pkgversion
    local xtlcmd
    local cmd
    local lm x=$((prdstk_level*2))
    [ $x -eq 0 ]||eval printf -v lm '%.s-' {1..$x}
    do_1_status_package "$@"
    if [ $? -ne $STS_SUCCESS ]; then
      local xtlcmd=$(get_act_xtlcmd $2 install $3 $4 "$5")
      if [ -z "$xtlcmd" ]; then
        xtlcmd=$(get_act_xtlcmd $2 "" $3 $4 "$5")
      fi
      cmd="wlog \"$lm$pkgname should be installed by $xtlcmd\""
    else
      pkgversion=$(get_pkg_ver $2 $3 $4 "$5")
      if [ -z "$pkgversion" ]; then
        pkgversion="$pkgname: unknown version"
      fi
      cmd="wlog \"$lm$pkgname: $pkgversion\""
    fi
    eval $cmd
    return $STS_SUCCESS
}

do_dry_whatis_package() {
# do_dry_whatis_package(action pkgname OPTS [param] [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_whatis_package($1 $2 $3)">>~/$THIS.his
    local cmdname lm msg svcname vfycmd xtlcmd x=$((prdstk_level*2))
    [ $x -eq 0 ]||eval printf -v lm '%.s-' {1..$x}
    xtlcmd=$(get_all_xtlcmd $1 $2 $3 $4 "$5")
    svcname=$(get_prm_value "${2}_svcname")
    cmdname=$(get_prm_value "${2}_cmdname")
    vfycmd=$(get_prm_value "${cmdname}_vfycmd")
    if [ -n "$vfycmd" ]; then
      msg="$lm$2 (by $xtlcmd)($vfycmd)"
    else
      msg="$lm$2 (by $xtlcmd)"
    fi
    test_msg "$msg"
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return(0) #do_dry_whatis_package">>~/$THIS.his
    return $STS_SUCCESS
}

do_deploy_package() {
# do_deploy_package(action pkgname OPTS tarball LocalTmp)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_deploy_package($1 $2 $3 $4 $5)">>~/$THIS.his
    local pkgname=$2
    local tarball=$4
    local LocalTmp=$5
    local cmd OPTS s sts
    [ $opt_dry_run -gt 0 ] && OPTS=${OPTS}n
    [ $opt_verbose -gt 0 ] && OPTS=${OPTS}v
    [ -n "$OPTS" ] && OPTS=-${OPTS}
    if [ $test_mode -gt 0 ]; then
      cd $RUNDIR
    elif [ -d "$LocalTmp" ]; then
      cd $LocalTmp
    fi
    if [ "${LocalTmp:0:4}" == "/tmp" ]; then
      if [ -d "$LocalTmp/$pkgname" ]; then
        run_traced "rm -fR $LocalTmp/$pkgname"
      elif [ -f "$LocalTmp/$pkgname" ]; then
        run_traced "rm -f $LocalTmp/$pkgname"
      fi
    fi
    if [ "${tarball: -4}" == ".rpm" ]; then
      s="$pkgname~~~~~~yum"
      cmd=$(get_full_xtlcmd $1 $tarball "$3" "" "$s")
      run_traced "$cmd"
      sts=$?
    elif [ "${tarball: -4}"  == ".deb" ]; then
      s="$pkgname~~~~~~apt-get"
      cmd=$(get_full_xtlcmd $1 $tarball "$3" "" "$s")
      if [ ${UPDATED:-0} -eq 0 ] && [[  $cmd =~ apt-get.*upgrade ]]; then
        if [[ $3 =~ y ]]; then cmd0="apt-get -y update"; else cmd0="apt-get update"; fi
        run_traced "$cmd0"
        UPDATED=1
      fi
      run_traced "$cmd"
      sts=$?
    elif [ "${pkgname:0:7}" == "python-" ]; then
      local s="$pkgname~~~~~~pip"
      local cmd=$(get_full_xtlcmd $1 $tarball "$3" "" "$s")
      run_traced "$cmd"
      sts=$?
    else
      if [ -d ./$pkgname ]; then
        run_traced "rm -fR ./$pkgname"
      fi
      run_traced "tar -xf $tarball"
      if [ -d ./$pkgname ]; then
        run_traced "cd ./$pkgname"
      fi
      if [ "$pkgname" == "lisa" -a "$TDIR" != "/usr/bin" -a ! -f ./setup.sh ]; then
        create_setup lisa $SETUP
      fi
      if [ -f ./setup.sh ]; then
        [ $opt_dry_run -gt 0 ] && echo "> ./setup.sh $OPTS"
        [ $opt_dry_run -eq 0 -a $opt_verbose -gt 0 ] && echo "\$ ./setup.sh $OPTS"
        ./setup.sh $OPTS
        sts=$?
      elif [ -f ./setup.py ]; then
        run_traced "python ./setup.py"
        sts=$?
      else
        s="$pkgname~~~~~~pip"
        cmd=$(get_full_xtlcmd $1 $tarball "$3" "" "$s")
        run_traced "$cmd"
        sts=$?
      fi
    fi
    return $sts
}

do_dry_install_lisa() {
# check if 1.st installation 
    local OPTS sts SETUP
    [ $opt_dry_run -gt 0 ] && OPTS=${OPTS}n
    [ $opt_verbose -gt 0 ] && OPTS=${OPTS}v
    [ -n "$OPTS" ] && OPTS=-${OPTS}
    if [ -f ./setup.sh ]; then
      SETUP=./setup.sh
    elif [ -f $TDIR/setup.sh ]; then
      SETUP=$TDIR/setup.sh
    elif [ "$TDIR" != "/usr/bin" -a ! -f ./setup.sh ]; then
      SETUP=./setup.sh
      create_setup lisa $SETUP
    fi
    if [ "$TDIR" != "/usr/bin" -a -f $SETUP ]; then
      [ $opt_dry_run -gt 0 ] && echo "> $SETUP $OPTS"
      [ $opt_dry_run -eq 0 -a $opt_verbose -gt 0 ] && echo "\$ $SETUP $OPTS"
      $SETUP $OPTS
      sts=$?
    else
      echo "!! File ./setup.sh not found!"
      sts=$STS_FAILED
    fi
    return $sts
}

do_dry_install_by_wget() {
# do_dry_install_by_wget(action pkgname OPTS)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_install_by_wget($1 $2 $3)">>~/$THIS.his
    local sts
    local pkgname=$2
    local xtlcmd=wget
    local LocalRoot="$(get_prm_value ${pkgname}_${xtlcmd}_LocalRoot)"
    local LocalTmp="$(get_prm_value ${pkgname}_${xtlcmd}_LocalTmp)"
    local pkg_URL=$(get_prm_value ${pkgname}_${xtlcmd}_URL)
    local tarball=$(get_prm_value ${pkgname}_${xtlcmd}_xname)
    local pkg_md5="$(get_prm_value ${pkgname}_${xtlcmd}_md5)"
    if [ -z "$tarball" -o -z "$pkg_URL" ]; then
      elog "*** Package $pkgname not found for this OS version"
      elog "    wget $tarball from URL=$pkg_URL"
      return 2
    fi
    if [ -n "$LocalRoot" ]; then
      LocalTmp=
      if [ ! -d $LocalRoot ]; then
        mkdir_traced $LocalRoot
      fi
      run_traced "cd $LocalRoot"
    else
      if [ -z "$LocalTmp" ]; then
        LocalTmp=/tmp/$pkgname
      fi
      run_traced "cd /tmp"
    fi
    if [ -f "./$tarball" ]; then
      run_traced "rm -f ./$tarball"
    fi
    run_traced "wget $pkg_URL/$tarball$pkg_md5"
    do_deploy_package $1 $pkgname "$3" $tarball "$LocalRoot$LocalTmp"
    sts=$?
    run_traced "rm -f $tarball"
    return $?
}

post_dry_config_service() {
# post_dry_config_service(action pkgname OPTS)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "post_dry_config_service($1 $2 $3)">>~/$THIS.his
    local svcname=$(get_prm_value "${2}_svcname")
    local cmd=$(get_full_svccmd restart $svcname $3)
    run_traced "$cmd"
    cmd=$(get_full_autosvc on $svcname $3)
    run_traced "$cmd"
    local sts=$?
    return $sts
}

post_dry_install_service() {
# post_dry_install_service(action pkgname OPTS)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "post_dry_install_service($1 $2 $3)">>~/$THIS.his
    local init_cmd=$(get_prm_value "${2}_init_svcname")
    local svcname=$(get_prm_value "${2}_svcname")
    local act cmd svcname sts x
    if [ -n "$init_cmd" ]; then
      if [ "${init_cmd:0:7}" == "service" ]; then
        read cmd x act <<< $init_cmd
        init_cmd=$(get_full_svccmd $act $svcname $3)
      fi
      run_traced "$init_cmd"
    fi
    local cmd=$(get_full_svccmd start $svcname $3)
    run_traced "$cmd"
    local sts=$?
    if [ $sts -eq $STS_SUCCESS ]; then
      run_traced "sleep 3"
      cmd=$(get_full_autosvc on $svcname $3)
      run_traced "$cmd"
      sts=$?
    fi
    if [ $sts -eq $STS_SUCCESS ]; then
      local LocalRoot="$(get_prm_value ${2}_LocalRoot)"
      if [ -n "$LocalRoot" ]; then
        if [ ! -d $LocalRoot ]; then
          mkdir_traced $LocalRoot
        fi
      fi
    fi
    return $sts
}

do_dry_config_package() {
# do_dry_config_package(action pkgname OPTS)
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_config_package($1 $2 $3)">>~/$THIS.his
    local confn=$(get_full_confn $1 $2 $3)
    if [ -n "$confn" ]; then
      if [ $opt_dry_run -eq 0 ]; then
        vim $confn
      else
        echo "> vim $confn"
      fi
    fi
    return $STS_SUCCESS
}

do_dry_install_package() {
# do_dry_install_package(action pkgname OPTS [param] [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_install_package($1 $2 $3)">>~/$THIS.his
    local cmd pkgname xcmd
    cmd=$(get_full_xtlcmd $1 $2 $3 $4 "$5")
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  cmd=$cmd;">>~/$THIS.his
    run_traced "$cmd"
    cmd=$(get_act_xtlcmd $2 "$1" "$3" "$4" "$5")
    pkgname=$(get_realname "$2" "update" $3)
    # [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  $pkgname=get_realname($2 update $3);">>~/$THIS.his
    xcmd=$(get_act_xtlcmd $pkgname "update" "$3" "$4" "$5")
    # [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  $xcmd=get_act_xtlcmd($pkgname update $3 $4 $5);">>~/$THIS.his
    if [ "$cmd" != "$xcmd" -a "$xcmd" == "pip" ]; then
      xcmd=$(get_full_xtlcmd "update" $pkgname $3 $4 "$5")
      run_traced "$xcmd"
    fi
    return $?
}

do_1_tellme_package() {
# do_1_tellme_package(action pkgname OPTS param [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_1_tellme_package($1 $2 $3 $4)">>~/$THIS.his
    local pkg lop lreqver rop rreqver branch x xtlcmd
    IFS="~" read pkg lop lreqver rop rreqver branch x<<<"$5"
    local pkgname=$(get_realname "$2" "" $3)
    local sts=$STS_SUCCESS
    if [ "$4" == "realname" ]; then
      local iter=$(get_iter $pkgname $3)
      if [ "$2" == "$iter" ]; then
        echo "$2"
      elif [ "$pkgname" == "$iter" ]; then
        echo "$2=$pkgname"
      else
        echo "$2=($iter)"
      fi
      [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return(0) #do_1_tellme_package">>~/$THIS.his
      return $STS_SUCCESS
    elif [ "$4" == "installer" ]; then
      local xtlcmd=$(get_act_xtlcmd $pkgname install $3 $4 "$5")
      echo "$xtlcmd"
    elif [ "$4" == "vfycmd" ]; then
      local vfycmd=$(get_prm_value "${2}_vfycmd")
      echo "$vfycmd"
    elif [ "$4" == "confn" ]; then
      local confn=$(get_full_confn $1 $pkgname $3)
      echo "$confn"
    elif [ "$4" == "cmdname" ]; then
      local cmdname=$(get_prm_value "${2}_cmdname")
      echo "$cmdname"
    elif [ "$4" == "branches" ]; then
      local p=$(get_prm_value "${2}_Branch")
      echo "$p"
    elif [ "$4" == "submodules" ]; then
      local p=$(get_prm_value "${2}_SubPkgList")
      echo "$p"
    elif [ "$4" == "requirements" ]; then
      local p=$(get_prm_value "${2}_ReqPkgList")
      echo "$p"
    elif [ "$4" == "confdirs" ]; then
      local p=$(get_prm_value "${2}_confdirs")
      echo "$p"
    elif [ "$4" == "localroot" ]; then
      local p=$(get_full_LocalRoot 'install' $pkgname)
      echo "$p"
    elif [ "$4" == "localdir" ]; then
      local xtlcmd=$(get_act_xtlcmd $pkgname install $3 $4 "$5")
      x="$(get_prm_value ${pkgname}_${xtlcmd}_LocalDir)"
      [ -n "$x" ] && p=$(expand_param $x $branch)
      echo "$p"
    elif [ "$4" == "infn" ]; then
      echo "$PKG_CONF $SAMPLE"
    else
      echo "Invalid param: branches|cmdname|confdirs|confn|infn|installer|localdir|localroot|realname|requirements|submodules|vfycmd"
      sts=$STS_FAILED
    fi
    return $sts
}

do_1_status_package() {
# do_1_status_package(action pkgname OPTS [param] [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_1_status_package($1 $2 $3 $4 $5)">>~/$THIS.his
    local pkgversion=$(get_pkg_ver $2 $3 $4 "$5")
    [ -n "$pkgversion" ]
    return $?
}

do_1_info_package() {
# do_1_info_package(action pkgname OPTS [param] [SPECIMEN])
# - info receives cmdline pkgname, no realname
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_1_info_package($1 $2 $3)">>~/$THIS.his
    local pkgname=$(get_realname "$2" "" $3)
    local pkglist=$(please tellme $2 $3 realname)
    local p=$(get_realname "$2" "install" $3)
    local xtlcmd=$(get_all_xtlcmd "" $p $3 $4 "$5")
    local svcname=$(get_prm_value "${pkgname}_svcname")
    local cmdname=$(get_prm_value "${pkgname}_cmdname")
    local vfycmd=$(get_prm_value "${cmdname}_vfycmd")
    local req=$(get_prm_value "${2}_ReqPkgList")
    if [[ $pkglist =~ = ]]; then
      echo "$pkglist (install by $xtlcmd)"
    else
      if [ "$2" == "$pkgname" ]; then
        echo "$2 (install by $xtlcmd)"
      else
        echo "$2=$pkgname (install by $xtlcmd)"
      fi
    fi
    [ -n "$svcname" ] && echo "  service=$svcname"
    [ -n "$cmdname" ] && echo "  command=$cmdname"
    [ -n "$vfycmd" ] && echo "  get_ver=$vfycmd"
    [ -n "$req" ] && echo "  requirements=$req"
    [ -n "$PKG_CONF" ] && echo "  infn=$PKG_CONF $SAMPLE"
    return $STS_SUCCESS
}

do_dry_remove_package() {
# do_dry_remove_package(action pkgname OPTS [param] [SPECIMEN])
     [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_dry_remove_package($1 $2 $3)">>~/$THIS.his
    local cmd=$(get_full_xtlcmd $1 $2 $3 $4 "$5")
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  cmd=$cmd;">>~/$THIS.his
    run_traced "$cmd"
    return $?
}

do_local_function() {
# do_local_function(prefix svcname action pkgname OPTS [param] [SPECIMEN])
    [[ $5 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_local_function($1 $2 $3 $4 $5 $6 $7)">>~/$THIS.his
    local act c cmd d D ij pkg s sts=127 x X xtlcmd
    if [[ $5 =~ I ]]; then
      ij=I
    elif [[ $5 =~ J ]]; then
      ij=J
    else
      ij=
    fi
    act=$3
    pkg=$4
    if [ "$1" == "pre_" -a -z "$ij" ] && [[ " install update remove " =~ $3 ]]; then
      do_1_status_package $3 $pkg $5 "$6" "$7"
      sts=$?
      if [ "$3" == "install" ]; then
        if [ $sts -ne $STS_SUCCESS ]; then
          sts=$STS_SUCCESS
        elif $(is_virtualname $4); then
          if [ $opt_aaeD -lt 2 ]; then
             opt_aaeD=2
          fi
          if [ $opt_aaeR -lt 2 ]; then
            opt_aaeR=2
          fi
        else
          if [ $opt_aaeD -eq 1 -a $prdstk_level -gt 0 ] || [ $opt_aaeR -eq 1 -a $prdstk_level -eq 0 ]; then
            ij=I
            DETECTED_YES="$DETECTED_YES $4"
          elif [ $opt_aaeD -eq 2 -a $prdstk_level -gt 0 ] || [ $opt_aaeR -eq 2 -a $prdstk_level -eq 0 ]; then
            ij=I
            DETECTED_YES="$DETECTED_YES $4"
          elif [ $opt_aaeD -eq 3 -a $prdstk_level -gt 0 ] || [ $opt_aaeR -eq 3 -a $prdstk_level -eq 0 ]; then
            act="update"
            DETECTED_UPD="$DETECTED_UPD $4"
          else
            [[ $5 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($STS_FAILED) #do_local_function">>~/$THIS.his
            return $STS_FAILED
          fi
        fi
      elif [ "$3" == "update" -a $sts -ne $STS_SUCCESS ]; then
        sts=$STS_SUCCESS
        if [ $opt_aaeD -eq 1 -a $prdstk_level -gt 0 ] || [ $opt_aaeR -eq 1 -a $prdstk_level -eq 0 ]; then
          ij=J
          DETECTED_NO="$DETECTED_NO $4"
        elif [ $opt_aaeD -ge 2 -a $prdstk_level -gt 0 ] || [ $opt_aaeR -ge 2 -a $prdstk_level -eq 0 ]; then
          act="install"
          DETECTED_INS="$DETECTED_INS $4"
        else
          [[ $5 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($STS_FAILED) #do_local_function">>~/$THIS.his
          return $STS_FAILED
        fi
      elif [ "$3" == "remove" -a $sts -ne $STS_SUCCESS ]; then
        sts=$STS_SUCCESS
        if [ $opt_aaeD -ge 1 -a $prdstk_level -gt 0 ] || [ $opt_aaeR -ge 1 -a $prdstk_level -eq 0 ]; then
          ij=J
          DETECTED_NO="$DETECTED_NO $4"
        else
          [[ $5 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($STS_FAILED) #do_local_function">>~/$THIS.his
          return $STS_FAILED
        fi
      elif [ "$3" == "remove" -a $prdstk_level -ge $opt_depth ]; then
        ij=J
        DETECTED_NO="$DETECTED_NO $4"
      fi
    fi
    if $(is_virtualname $4); then
      X="_by_NULL"
    else
      xtlcmd=$(get_act_xtlcmd $4 $act $5 $6 "$7")
      if [ -n "$2" ]; then
        X="_$4 _by_$xtlcmd _service _package"
      else
        X="_$4 _by_$xtlcmd _package"
      fi
    fi
    for c in _${act} _; do
      for x in $X; do
        if [ "$x" == "_service" -o "$x" == "_package" -o $opt_dry_run -eq 0 ]; then
          D="_dry _"
        else
          D="_dry"
        fi
        for d in $D; do
          cmd=${1}$d$c$x
          cmd=${cmd//__/_}
          if [ "$(type -t $cmd)" == "function" ]; then
            eval $cmd $act $4 $5$ij $6 "'$7'"
            s=$?
            [ $sts -eq 127 ] && sts=$s
            [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
            if [ $sts -eq $STS_SUCCESS ]; then
              cmd=${1}_${act}_ports
              cmd=${cmd//__/_}
              if [ "$(type -t $cmd)" == "function" ]; then
                eval $cmd $act $4 $5$ij $6 "'$7'"
                s=$?
                [ $sts -eq 127 ] && sts=$s
                [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
              fi
            fi
            [[ $5 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #do_local_function">>~/$THIS.his
            return $sts
          fi
        done
      done
    done
    cmd=${1}_${act}_ports
    cmd=${cmd//__/_}
    if [ "$(type -t $cmd)" == "function" ]; then
      eval $cmd $act $4 $5$ij $6 "$7"
      s=$?
      [ $sts -eq 127 ] && sts=$s
      [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
    fi
    [[ $5 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #do_local_function">>~/$THIS.his
    return $sts
}

do_local_function_BOP() {
# do_local_function_BOP(prefix svcname action pkgname OPTS [param] [SPECIMEN])
    [[ $5 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_local_function_BOP($1 $2 $3 $4 $5 $6 $7)">>~/$THIS.his
    local s sts=127
    if [ "$1" == "pre_" ]; then
      satisfy_request $3 $4 $5 $6 "$7"
      sts=$?
    fi
    do_local_function "$1" "$2" $3 $4 $5 $6 "$7"
    s=$?
    [ $sts -eq 127 ] && sts=$s
    [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$s
    if [ $sts -eq $STS_SUCCESS -o $sts -eq 127 ]; then
      local BOPlist="$(get_prm_value ${4}_BOP list)"
      [[ $5 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " ">>> BOPlist=$BOPlist [ $prdstk_level -lt $opt_depth ]">>~/$THIS.his
      if [ -n "$BOPlist" ] && [ $prdstk_level -lt $opt_depth ]; then
        local opts=${5//I/}
        opts=${opts//J/}
        do_act_all_deppkgs "$1" $3 $4 $opts $6 "$7"
        sts=$?
      fi
    fi
    [[ $5 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #do_local_function_BOP">>~/$THIS.his
    return $sts
}

do_iter_prod() {
# do_iter_prod(action pkgname OPTS [param] [SPECIMEN])
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "do_iter_prod($1 $2 $3 $4 $5)">>~/$THIS.his
    local pfx=$(get_pfxaction $1)
    local act=$(get_realaction $1)
    local pkgname=$(get_realname $2 $act $3)
    local iter p specimen x
    if [[ $3 =~ [qQ] ]]; then
      return 127
    fi
    set_from_conf_pkg "$2" "" $3
    if [ "$act" == "info" ]; then
      return 127
    fi
    iter=$(get_iter $pkgname $3)
    if [ "$act" == "tellme" ]; then
      specimen=$(get_specimen $2 "$act" $3)
      do_1_tellme_package $1 $2 "$3" "$4" "$specimen"
      return $?
    fi
    if [ "$pkgname" == "$iter" ]; then
      return 127
    fi
    for p in $iter; do
      if [ "$p" == "$2" -o "$p" == "$pkgname" ]; then
         if [[ $3 =~ b ]]; then local x="-Qb"; else local x="-Q"; fi
         please $1 $p "$x" "$4" "$5"
       else
         please $1 $p "$3" "$4" "$5"
       fi
       local sts=$?
       if [ $sts -ne $STS_SUCCESS ]; then
         break
       fi
    done
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #do_iter_prod">>~/$THIS.his
    return $sts
}

please() {
# please(action pkgname OPTS [param] [SPECIMEN])
# return: sts
# TODO [Debian] aptitude search 'pkgname'
# TODO [RHEL] yum search 'pkgname'
    if [ -z "$prdstk_level" ]; then prdstk_level=0; fi
    if [ -z "$conf_level" ]; then conf_level=0; fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "please($1 $2 $3 $4 $5)">>~/$THIS.his
    local act BOPcmd BOPcmd1 BOPcmd2 ij lm pfx pkgname s specimen svcname x
    do_iter_prod "$@"
    local sts=$?
    if [ $sts -ne 127 ]; then
      return $sts
    fi
    set_from_conf_pkg "$2" "" $3
    sts=$STS_SUCCESS
    x=$((prdstk_level*2))
    [ $x -eq 0 ]||eval printf -v lm '%.s-' {1..$x}
    pfx=$(get_pfxaction $1)
    act=$(get_realaction $1)
    if [ "$act" == "info" ]; then
      pkgname=$2
    else
      pkgname=$(get_realname $2 "$act" $3)
    fi
    specimen=$(get_specimen $2 "$act" $3)
    if [ -n "$5" -a -z "$specimen" ]; then
      specimen="$5"
    fi
    BOPcmd=do_dry_${act}_package
    BOPcmd1=do_1_${act}_package
    BOPcmd2=
    svcname=$(get_prm_value "${pkgname}_svcname")
    if [ -z "$pfx" ]; then
      if [ "$(type -t $BOPcmd1)" == "function" ] && [[  "$3" =~ q ]]; then
        eval $BOPcmd1 $act $pkgname $3 $4 "$specimen"
        return $?
      elif [ "$(type -t $BOPcmd1)" == "function" -a "$(type -t $BOPcmd)" != "function" ]; then
        eval $BOPcmd1 $act $pkgname $3 $4 "$specimen"
        return $?
      fi
    fi
    if [ -z "$pfx" -o "$pfx" == "pre_" ]; then
      [ $sts -eq $STS_SUCCESS ] && do_local_function_BOP "pre_" "$svcname" "$act" "$pkgname" "$3" "$4" "$specimen"
      s=$?
      [ $sts -eq $STS_SUCCESS ] && [ $s -ne 127 ] && [ $s -ne $STS_SUCCESS ] && sts=$s
    fi
    if [ "$pfx" != "pre_" ]; then
      if [[ " $DETECTED_YES " =~ [[:space:]]$pkgname[[:space:]] ]]; then
        local ij=I
      elif [[ " $DETECTED_NO " =~ [[:space:]]$pkgname[[:space:]] ]]; then
        local ij=J
      elif [[ " $DETECTED_UPD " =~ [[:space:]]$pkgname[[:space:]] ]]; then
        act="update"
      elif [[ " $DETECTED_INS " =~ [[:space:]]$pkgname[[:space:]] ]]; then
        act="install"
      fi
    fi
    if [ -z "$pfx" -o "$pfx" == "do_" ]; then
      [ $sts -eq $STS_SUCCESS ] && do_local_function_BOP "do_" "$svcname" "$act" "$pkgname" "$3$ij" "$4" "$specimen"
      s=$?
      [ $sts -eq $STS_SUCCESS ] && [ $s -ne $STS_SUCCESS ] && sts=$?
    fi
    if [ -z "$pfx" -o "$pfx" == "post_" ]; then
      [ $sts -eq $STS_SUCCESS ] && do_local_function_BOP "post_" "$svcname" "$act" "$pkgname" "$3$ij" "$4" "$specimen"
      s=$?
      [ $sts -eq $STS_SUCCESS ] && [ $s -ne 127 ] && [ $s -ne $STS_SUCCESS ] && sts=$?
    fi
    if [ $sts -ne $STS_SUCCESS ] || [[  "$3" =~ q ]]; then
      return $sts
    fi
    if [ $prdstk_level -eq 0 ]; then
      BOPcmd=end_${act}
      if [ "$(type -t $BOPcmd)" == "function" ]; then
        eval $BOPcmd $act $3$ij $4 $5
      fi
    fi
    [[ $3 =~ b ]] && printf "%${prdstk_level}.${prdstk_level}s %s\n" " " "  return($sts) #please">>~/$THIS.his
    return $sts
}


# main
OPTOPTS=(h        b       c        D       d         E       e         g       H        I        i      J        L         l          m        N        n           o       p         P          q           s         T         u       V           v           y       1)
OPTDEST=(opt_help opt_dbg opt_conf opt_dev opt_depth opt_osf opt_aae   opt_grf opt_host opt_xtly opt_if opt_xtln opt_log   opt_locale opt_mult opt_nake opt_dry_run opt_oed opt_nopip opt_pwd    opt_verbose opt_sub   test_mode use2xtl opt_version opt_verbose opt_yes opt_1st)
OPTACTI=(1        1       "="      "*"     "="       "="     "="       1       "="      "="      "="    "="      "="       "="        1        1        1           "="     1         "="        0           "="       1         "="     "*"         "+"         1       1)
OPTDEFL=(1        0       ""       ""      ""        ""      ""        0       ""       ""       ""     ""       ""        ""         0        0        0           ""      0         ""         -1          ""        0         ""      ""          -1           0       0)
OPTMETA=("help"   "dbg"   "file"   "dev"   "number"  "disto" "0-4,0-4" "win"   "host"   "pkg(s)" "file" "pkg(s)" "logfile" "iso3166"  "multi"  ""       "noop"      "disto" "pip"     "password" "quiet"     "modules" "teste"   "file"  "version"   "verbose"   "yes"   "first")
OPTHELP=("this help, type '$THIS help' for furthermore info"\
 "trace to build configuration"\
 "configuration file (def /etc/lisa/lisa.conf)"\
 "add development package(s)"\
 "max depth level"\
 "emulate Linux distribution; may be Ubuntu[12|14],CentOS[6|7]. Use carefully!"\
 "action on parent and dependecies if error: (0=exit, 1=ignore, 2=if upd do install, 3=do upd/install)"\
 "add graphical interface package(s)"\
 "[user@]host to export config files"\
 "declare package(s) as installed"\
 "use identity file (private key) for remote authentication"\
 "declare package(s) as uninstalled"\
 "log file name (def /var/log/lisa.log)"\
 "include localization module for odoo; use ISO3166 country code"\
 "multiple version environment (append version to filenames)"\
 "do nake installation (do not install submodules)"\
 "do nothing (dry-run)"\
 "select odoo family distribution; may be odoo,oca,zeroincombenze"\
 "do not use pip if found std package "\
 "default for users, if added by import command"\
 "quiet mode"\
 "install odoo submodules"\
 "test mode (implies dry-run)"\
 "use file to install"\
 "show version end exit"\
 "verbose mode"\
 "assume yes"\
 "1st installation")
OPTARGS=(action pkg param1 param2 param3)

parseoptargs "$@"
if [ "$opt_version" ]; then
  echo "$__version__"
  exit 0
fi
if [ $opt_help -gt 0 ]; then
  print_help "Build an Odoo or LAMP server"\
  "(C) 2015-2017 by zeroincombenze(R)\nhttp://www.zeroincombenze.it\nAuthor: antoniomaria.vigliotti@gmail.com"
  exit 0
fi
if [ "$DEV_ENVIRONMENT" == "$THIS" ]; then
  test_mode=1
fi
if [ "$opt_log" ]; then
  FLOG=$opt_log
elif [ $test_mode -gt 0 -o $opt_dry_run -gt 0 ]; then
  FLOG=~/$THIS.log
elif [ $EUID -eq 0 ]; then
  FLOG=/var/log/$THIS.log
else
  FLOG=~/$THIS.log
fi
get_arch "$opt_osf"
set_tlog_file "$FLOG" "" $OPTS
if [ -n "$opt_odoo" ]; then
  if [ "opt_oed" != "odoo" -a "opt_oed" != "oca" -a "opt_oed" != "zeroincombenze" ]; then
    opt_oed="zeroincombenze"
  fi
fi
opt_aaeR=$(echo "0$opt_aae"|awk -F, '{print $1}')
opt_aaeD=$(echo "$opt_aae,2"|awk -F, '{print $2}')
UPDATED=0

for tid in 0 1 2 3; do
  CFG_init $tid
done
prdstk_level=0
conf_level=0
declare -a CONF_FNS
conf_default
# link_cfg $FCONF $FCONFDEF
if [ ${opt_dbg:-0} -gt 0 ]; then rm -f ~/$THIS.his; fi
OPTS="-$opt_dev"
if [ ${opt_grf:-0} -gt 0 ]; then OPTS="${OPTS}g"; fi
if [ ${opt_dbg:-0} -gt 0 ]; then OPTS="${OPTS}b"; fi
if [ ${opt_nopip:-0} -gt 0 ]; then OPTS="${OPTS}p"; fi
OPTS=${OPTS//--/-}
# get_conf_pkg lisa.conf "" "" $OPTS
set_from_conf_pkg "lisa" "lisa.conf" $OPTS
if [ -n "$opt_xtly" ]; then
  SIMULATE_YES=" ${opt_xtly//,/ } "
fi
if [ -n "$opt_xtln" ]; then
  SIMULATE_NO=" ${opt_xtln//,/ } "
fi
# init_cfg_pkg "$pkg"
if [ $test_mode -gt 0 -a "$action" != "update" ]; then
  if [[ " $SIMULATE_YES " =~ [[:space:]]apache2[[:space:]] ]]; then
    SIMULATE_YES="$SIMULATE_YES port_tcp_80 port_tcp_443 port_tcp_21"
  elif [[ " $SIMULATE_YES " =~ [[:space:]]httpd[[:space:]] ]]; then
    SIMULATE_YES="$SIMULATE_YES port_tcp_80 port_tcp_443 port_tcp_21"
  fi
  if [[ " $SIMULATE_NO " =~ [[:space:]]apache2[[:space:]] ]]; then
    SIMULATE_NO="$SIMULATE_NO port_tcp_80 port_tcp_443 port_tcp_21"
  elif [[ " $SIMULATE_NO " =~ [[:space:]]httpd[[:space:]] ]]; then
    SIMULATE_NO="$SIMULATE_NO port_tcp_80 port_tcp_443 port_tcp_21"
  fi
  if [[ " $SIMULATE_YES " =~ [[:space:]]openssh[[:space:]] ]]; then
    SIMULATE_YES="$SIMULATE_YES port_tcp_22"
  elif [[ " $SIMULATE_YES " =~ [[:space:]]openssh-server[[:space:]] ]]; then
    SIMULATE_YES="$SIMULATE_YES port_tcp_22"
  fi
  if [[ " $SIMULATE_NO " =~ [[:space:]]openssh[[:space:]] ]]; then
    SIMULATE_NO="$SIMULATE_NO port_tcp_22"
  elif [[ " $SIMULATE_NO " =~ [[:space:]]openssh-server[[:space:]] ]]; then
    SIMULATE_NO="$SIMULATE_NO port_tcp_22"
  fi
fi
if [ -z "$opt_depth" ]; then
  opt_depth=999
fi
if [ "$action" == "remove" -a $opt_depth -eq 999 ]; then
  if $(is_virtualname $pkg); then
    opt_depth=0
  else
    opt_depth=1
  fi
fi
if [ $test_mode -gt 0 ]; then
  opt_dry_run=1
  opt_verbose=1
  if [ $opt_depth -eq 999 ]; then
    if $(is_virtualname $pkg); then
      opt_depth=0
    else
      opt_depth=1
    fi
  fi
  opt_yes=1
fi
if [ ${opt_yes:-0} -gt 0 ]; then OPTS="${OPTS}y"; fi
wlog "# $THIS $__version__ running on $(xuname -a)"
wlog "# Setup for $FH family"

if [ "$action" == "help" ]; then
  man $TDIR/$THIS.man
elif [ "$action" == "prepare" ]; then
  if [ $EUID -ne 0 ]; then
    elog "This command must be executed by root"
    exit $STS_FAILED
  fi
  wlog "Preparining group list"
  build_groups_list
  wlog "Preparing user list"
  build_user_list
  ending_msg
elif [ "$action" == "export" ]; then
  if [ -z "$opt_host" ]; then
    elog "Missing destination host name: use -H host option"
  else
    please install $THIS
  fi
  ending_msg
elif [ "$action" == "import" ]; then
  if [ $EUID -ne 0 ]; then
    elog "This command must be executed by root"
    exit $STS_FAILED
  fi
  wlog "Creating groups"
  add_groups
  wlog "Creating users"
  add_users
  ending_msg
# elif [ "$action" == "debug" ]; then
#    echo "[options]">lisa.conf.sample
#    jy=0
#    p=""
#    while ((jy<${#DEFPRM[*]})); do
#      echo "${DEFPRM[jy]}=${DEFVAL[jy]}"
#      echo "${DEFPRM[jy]}=${DEFVAL[jy]}">>lisa.conf.sample
#     ((jy++))
#    done
else
  actions=${action//+/ }
  actions=${actions//,/ }
  pkgs=${pkg//+/ }
  pkgs=${pkgs//,/ }
  TITL_whatis="List managed packages"
  TITL_status="Check for installation status"
  TITL_version="Show installed package version"
  TITL_install="Install package(s)"
  TITL_update="Update/upgrade package(s)"
  TITL_remove="Remove package package(s)"
  TITL_config="Configure package(s)"
  SYSPRIV="config install remove status update version"
  OPTS1ST="install update"
  ECHOCMDS="config install remove status update version"
  TAILMSG="install remove status update"
  REP_UPDATED=0
  opt_tail=0
  if [ $opt_1st -gt 0 ]; then
    for act in $actions; do
      if [[ " $OPTS1ST " =~ [[:space:]]$act[[:space:]] ]]; then
        opt_tail=1
        break
      fi
    done
  fi
  if [ $opt_tail -gt 0 ]; then
    wlog "Setup for 1st installation"
    if [ $opt_dry_run -eq 0 ]; then
      please update "." $OPTS
    fi
  fi
  opt_tail=0
  for act in $actions; do
    if [[ " $SYSPRIV " =~ [[:space:]]$act[[:space:]] ]]; then
      if [ $EUID -ne 0 -a $test_mode -eq 0 ]; then
        elog "This command must be executed by root"
        exit $STS_FAILED
      fi
    fi
    if [[ " $ECHOCMDS " =~ [[:space:]]$act[[:space:]] ]]; then
      set_tlog_file "$FLOG" "" "echo"
    fi
    if [ $test_mode -eq 0 ]; then
      X="TITL_$act"
      if [ -n "${!X}" ]; then
        print_title "${!X}"
      fi
    fi
    if [ -z "$param1" ]; then
      param1="base"
    fi
    for package in $pkgs; do
      please $act $package $OPTS "$param1" "$param2" "$param3"
      sts=$?
      if [ $sts -eq 127 ]; then
        echo "Unknow action $act"
        exit $sts
      elif [ $sts -ne $STS_SUCCESS ]; then
        echo "!!Error $sts in $act $package!"
        exit $sts
      fi
    done
    if [[ " $TAILMSG " =~ [[:space:]]$act[[:space:]] ]]; then
      opt_tail=1
    fi
  done
  if [ $opt_tail -gt 0 ]; then
    ending_msg
  fi
fi
exit 0
