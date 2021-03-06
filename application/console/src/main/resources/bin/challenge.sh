#!/bin/bash --posix
#
##############################################################################
# Source : runner of scala console 2.9.2.
# Copyright 2002-2011, LAMP/EPFL
#
# This is free software; see the distribution for copying conditions.
# There is NO warranty; not even for MERCHANTABILITY or FITNESS FOR A
# PARTICULAR PURPOSE.
##############################################################################


cygwin=false;
case "`uname`" in
    CYGWIN*) cygwin=true ;;
esac

# Finding the root folder for this App distribution
SOURCE=$0;
SCRIPT=`basename "$SOURCE"`;
while [ -h "$SOURCE" ]; do
    SCRIPT=`basename "$SOURCE"`;
    LOOKUP=`ls -ld "$SOURCE"`;
    TARGET=`expr "$LOOKUP" : '.*-> \(.*\)$'`;
    if expr "${TARGET:-.}/" : '/.*/$' > /dev/null; then
        SOURCE=${TARGET:-.};
    else
        SOURCE=`dirname "$SOURCE"`/${TARGET:-.};
    fi;
done;

# see #2092
APP_HOME=`dirname "$SOURCE"`
APP_HOME=`cd "$APP_HOME"; pwd -P`
APP_HOME=`cd "$APP_HOME"/..; pwd`

# Remove spaces from APP_HOME on windows
if $cygwin; then
    APP_HOME=`cygpath --windows --short-name "$APP_HOME"`
    APP_HOME=`cygpath --unix "$APP_HOME"`
fi

# Constructing the extension classpath
TOOL_CLASSPATH=""
if [ -z "$TOOL_CLASSPATH" ] ; then
    for ext in "$APP_HOME"/lib/* ; do
        if [ -z "$TOOL_CLASSPATH" ] ; then
            TOOL_CLASSPATH="$ext"
        else
            TOOL_CLASSPATH="$TOOL_CLASSPATH:$ext"
        fi
    done
fi

CYGWIN_JLINE_TERMINAL=
if $cygwin; then
    if [ "$OS" = "Windows_NT" ] && cygpath -m .>/dev/null 2>/dev/null ; then
        format=mixed
    else
        format=windows
    fi
    APP_HOME=`cygpath --$format "$APP_HOME"`
    TOOL_CLASSPATH=`cygpath --path --$format "$TOOL_CLASSPATH"`
    case "$TERM" in
        rxvt* | xterm*)
            stty -icanon min 1 -echo
            CYGWIN_JLINE_TERMINAL="-Djline.terminal=app.tools.jline.UnixTerminal"
        ;;
    esac
fi

[ -n "$JAVA_OPTS" ] || JAVA_OPTS="-Xmx256M -Xms32M"

# break out -D and -J options and add them to JAVA_OPTS as well
# so they reach the underlying JVM in time to do some good.  The
# -D options will be available as system properties.
declare -a java_args
declare -a app_args

# default to classpath. Try option -bootcp to switch the boot classpath. It's speedy.
CPSELECT="-classpath "

while [ $# -gt 0 ]; do
  case "$1" in
    -D*)
      # pass to app as well: otherwise we lose it sometimes when we
      # need it, e.g. communicating with a server compiler.
      java_args=("${java_args[@]}" "$1")
      app_args=("${app_args[@]}" "$1")
      shift
      ;;
    -J*)
      # as with -D, pass to app even though it will almost
      # never be used.
      java_args=("${java_args[@]}" "${1:2}")
      app_args=("${app_args[@]}" "$1")
      shift
      ;;
    -toolcp)
      TOOL_CLASSPATH="$TOOL_CLASSPATH:$2"
      shift 2
      ;;
    -bootcp)
      CPSELECT="-Xbootclasspath/a:"
      shift
      ;;
    *)
      app_args=("${app_args[@]}" "$1")
      shift
      ;;
  esac
done

# reset "$@" to the remaining args
set -- "${app_args[@]}"

if [ -z "$JAVACMD" -a -n "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
fi

"${JAVACMD:=java}" \
  -Dlog4j.configuration="log4j.properties" \
  $JAVA_OPTS \
  "${java_args[@]}" \
  ${CPSELECT}${TOOL_CLASSPATH}:${APP_HOME}"/conf/":${APP_HOME}"/" \
  -Dscala.usejavacp=true \
  -Dapp.home="$APP_HOME" \
  -Denv.emacs="$EMACS" \
  $CYGWIN_JLINE_TERMINAL \
  fasar.sortable.challenge.Main "$@"

# record the exit status lest it be overwritten:
# then reenable echo and propagate the code.

