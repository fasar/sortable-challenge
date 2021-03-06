@echo off

rem ##########################################################################
rem # Source : runner of scala console  2.9.2
rem # Copyright 2002-2011, LAMP/EPFL
rem #
rem # This is free software; see the distribution for copying conditions.
rem # There is NO warranty; not even for MERCHANTABILITY or FITNESS FOR A
rem # PARTICULAR PURPOSE.
rem ##########################################################################

if "%OS%" NEQ "Windows_NT" (
  echo "Sorry, your version of Windows is too old to run App."
  goto :eof
)

@setlocal
call :set_home

rem We use the value of the JAVACMD environment variable if defined
set _JAVACMD=%JAVACMD%

if "%_JAVACMD%"=="" (
  if not "%JAVA_HOME%"=="" (
    if exist "%JAVA_HOME%\bin\java.exe" set "_JAVACMD=%JAVA_HOME%\bin\java.exe"
  )
)

if "%_JAVACMD%"=="" set _JAVACMD=java

rem We use the value of the JAVA_OPTS environment variable if defined
set _JAVA_OPTS=%JAVA_OPTS%
if "%_JAVA_OPTS%"=="" set _JAVA_OPTS=-Xmx512M -Xms32M

set _TOOL_CLASSPATH=
if "%_TOOL_CLASSPATH%"=="" (
  for %%f in ("%_APP_HOME%\lib\*") do call :add_cpath "%%f"
  for /d %%f in ("%_APP_HOME%\lib\*") do call :add_cpath "%%f"
)

call :add_cpath "%_APP_HOME%\conf"
call :add_cpath "%_APP_HOME%\"

set _PROPS=-Dapp.home="%_APP_HOME%" -Denv.emacs="%EMACS%" -Dscala.usejavacp=true -Dlog4j.configuration="\guilog4j.conf"

rem echo "%_JAVACMD%" %_JAVA_OPTS% %_PROPS% -cp "%_TOOL_CLASSPATH%" fasar.sortable.challenge.Main  %*
"%_JAVACMD%" %_JAVA_OPTS% %_PROPS% -cp "%_TOOL_CLASSPATH%" fasar.sortable.challenge.Main  %*
goto end

rem ##########################################################################
rem # subroutines

:add_cpath
  if "%_TOOL_CLASSPATH%"=="" (
    set _TOOL_CLASSPATH=%~1
  ) else (
    set _TOOL_CLASSPATH=%_TOOL_CLASSPATH%;%~1
  )
goto :eof

rem Variable "%~dps0" works on WinXP SP2 or newer
rem (see http://support.microsoft.com/?kbid=833431)
rem set _APP_HOME=%~dps0..
:set_home
  set _BIN_DIR=
  for %%i in (%~sf0) do set _BIN_DIR=%_BIN_DIR%%%~dpsi
  set _APP_HOME=%_BIN_DIR%..
goto :eof

:end
@endlocal
