## Install Bit coin :)  

- <a href="#source-build">Source build</a>

<div id="source-build"></div>  

### Source build  

> Git clone  

```
$ git clone https://github.com/bitcoin/bitcoin.git  
$ git branch
$ git checkout v0.16.2
$ git branch
```  

> install autoconf

```
$ ./autogen.sh
configuration failed, please install autoconf first
$ sudo apt-get install autoconf
```  

> again  

```
$ ./autogen.sh
configure.ac:28: installing 'build-aux/install-sh'
configure.ac:28: installing 'build-aux/missing'
Makefile.am:8: error: Libtool library used but 'LIBTOOL' is undefined
Makefile.am:8:   The usual way to define 'LIBTOOL' is to add 'LT_INIT'
Makefile.am:8:   to 'configure.ac' and run 'aclocal' and 'autoconf' again.
Makefile.am:8:   If 'LT_INIT' is in 'configure.ac', make sure
Makefile.am:8:   its definition is in aclocal's search path.
Makefile.am: installing 'build-aux/depcomp'
/usr/share/automake-1.15/am/depend2.am: error: am__fastdepCXX does not appear in AM_CONDITIONAL
/usr/share/automake-1.15/am/depend2.am:   The usual way to define 'am__fastdepCXX' is to add 'AC_PROG_CXX'
/usr/share/automake-1.15/am/depend2.am:   to 'configure.ac' and run 'aclocal' and 'autoconf' again
/usr/share/automake-1.15/am/depend2.am: error: AMDEP does not appear in AM_CONDITIONAL
/usr/share/automake-1.15/am/depend2.am:   The usual way to define 'AMDEP' is to add one of the compiler tests
/usr/share/automake-1.15/am/depend2.am:     AC_PROG_CC, AC_PROG_CXX, AC_PROG_OBJC, AC_PROG_OBJCXX,
/usr/share/automake-1.15/am/depend2.am:     AM_PROG_AS, AM_PROG_GCJ, AM_PROG_UPC
/usr/share/automake-1.15/am/depend2.am:   to 'configure.ac' and run 'aclocal' and 'autoconf' again
Makefile.am: error: C++ source seen but 'CXX' is undefined
Makefile.am:   The usual way to define 'CXX' is to add 'AC_PROG_CXX'
Makefile.am:   to 'configure.ac' and run 'autoconf' again.
parallel-tests: installing 'build-aux/test-driver'
autoreconf: automake failed with exit status: 1
```  

> install libtool  

```
$ sudo apt-get install libtool
```  

> Agent autogen.sh  

```
$ ./autogen.sh
libtoolize: putting auxiliary files in AC_CONFIG_AUX_DIR, 'build-aux'.
libtoolize: copying file 'build-aux/ltmain.sh'
libtoolize: putting macros in AC_CONFIG_MACRO_DIRS, 'build-aux/m4'.
libtoolize: copying file 'build-aux/m4/libtool.m4'
libtoolize: copying file 'build-aux/m4/ltoptions.m4'
libtoolize: copying file 'build-aux/m4/ltsugar.m4'
libtoolize: copying file 'build-aux/m4/ltversion.m4'
libtoolize: copying file 'build-aux/m4/lt~obsolete.m4'
configure.ac:45: installing 'build-aux/compile'
configure.ac:45: installing 'build-aux/config.guess'
configure.ac:45: installing 'build-aux/config.sub'
configure.ac:28: installing 'build-aux/missing'
Makefile.am: installing 'build-aux/depcomp'
libtoolize: putting auxiliary files in AC_CONFIG_AUX_DIR, 'build-aux'.
libtoolize: copying file 'build-aux/ltmain.sh'
libtoolize: putting macros in AC_CONFIG_MACRO_DIRS, 'build-aux/m4'.
libtoolize: copying file 'build-aux/m4/libtool.m4'
libtoolize: copying file 'build-aux/m4/ltoptions.m4'
libtoolize: copying file 'build-aux/m4/ltsugar.m4'
libtoolize: copying file 'build-aux/m4/ltversion.m4'
libtoolize: copying file 'build-aux/m4/lt~obsolete.m4'
configure.ac:10: installing 'build-aux/compile'
configure.ac:5: installing 'build-aux/config.guess'
configure.ac:5: installing 'build-aux/config.sub'
configure.ac:9: installing 'build-aux/install-sh'
configure.ac:9: installing 'build-aux/missing'
Makefile.am: installing 'build-aux/depcomp'
parallel-tests: installing 'build-aux/test-driver'
libtoolize: putting auxiliary files in AC_CONFIG_AUX_DIR, 'build-aux'.
libtoolize: copying file 'build-aux/ltmain.sh'
libtoolize: putting macros in AC_CONFIG_MACRO_DIRS, 'build-aux/m4'.
libtoolize: copying file 'build-aux/m4/libtool.m4'
libtoolize: copying file 'build-aux/m4/ltoptions.m4'
libtoolize: copying file 'build-aux/m4/ltsugar.m4'
libtoolize: copying file 'build-aux/m4/ltversion.m4'
libtoolize: copying file 'build-aux/m4/lt~obsolete.m4'
configure.ac:78: installing 'build-aux/compile'
configure.ac:28: installing 'build-aux/config.guess'
configure.ac:28: installing 'build-aux/config.sub'
configure.ac:38: installing 'build-aux/install-sh'
configure.ac:38: installing 'build-aux/missing'
src/Makefile.am: installing 'build-aux/depcomp'
parallel-tests: installing 'build-aux/test-driver'
```  

> ./configure  

```
$ ./configure
checking build system type... x86_64-pc-linux-gnu
checking host system type... x86_64-pc-linux-gnu
checking for a BSD-compatible install... /usr/bin/install -c
checking whether build environment is sane... yes
checking for a thread-safe mkdir -p... /bin/mkdir -p
checking for gawk... gawk
checking whether make sets $(MAKE)... no
checking whether make supports nested variables... no
checking whether to enable maintainer-specific portions of Makefiles... yes
checking whether make supports nested variables... (cached) no
checking for g++... no
checking for c++... no
checking for gpp... no
checking for aCC... no
checking for CC... no
checking for cxx... no
checking for cc++... no
checking for cl.exe... no
checking for FCC... no
checking for KCC... no
checking for RCC... no
checking for xlC_r... no
checking for xlC... no
checking whether the C++ compiler works... no
configure: error: in `/home/ubuntu/bitcoin':
configure: error: C++ compiler cannot create executables
```  

> ./configure  

```
$ ./configure  
configure: error: PKG_PROG_PKG_CONFIG macro not found. Please install pkg-config and re-run autogen.sh.
...
```  

> install libdb  

```
$ sudo apt-install pkg-config
$ sudo add-apt-repository ppa:bitcoin/bitcoin
$ sudo apt-get update
$ sudo apt-get install -y libdb4.8-dev libdb4.8++-dev
$ sudo apt-get install build-essential autoconf libssl-dev libboost-dev libboost-chrono-dev libboost-filesystem-dev libboost-program-options-dev libboost-system-dev libboost-test-dev libboost-thread-dev
$ sudo apt-get install libevent-dev
```   

> Make  

```
$ make
```  

> bitcoind 설치  

```
$ sudo make install
```  

> bitcoin.conf  

```
##
## bitcoin.conf configuration file. Lines beginning with # are comments.
##

# Network-related settings:

# Run on the test network instead of the real bitcoin network.
#testnet=3

# Run a regression test network
#regtest=0

# Connect via a SOCKS5 proxy
#proxy=127.0.0.1:9050

# Bind to given address and always listen on it. Use [host]:port notation for IPv6
#bind=<addr>

# Bind to given address and whitelist peers connecting to it. Use [host]:port notation for IPv6
#whitebind=<addr>

##############################################################
##            Quick Primer on addnode vs connect            ##
##  Let's say for instance you use addnode=4.2.2.4          ##
##  addnode will connect you to and tell you about the      ##
##    nodes connected to 4.2.2.4.  In addition it will tell ##
##    the other nodes connected to it that you exist so     ##
##    they can connect to you.                              ##
##  connect will not do the above when you 'connect' to it. ##
##    It will *only* connect you to 4.2.2.4 and no one else.##
##                                                          ##
##  So if you're behind a firewall, or have other problems  ##
##  finding nodes, add some using 'addnode'.                ##
##                                                          ##
##  If you want to stay private, use 'connect' to only      ##
##  connect to "trusted" nodes.                             ##
##                                                          ##
##  If you run multiple nodes on a LAN, there's no need for ##
##  all of them to open lots of connections.  Instead       ##
##  'connect' them all to one node that is port forwarded   ##
##  and has lots of connections.                            ##
##       Thanks goes to [Noodle] on Freenode.               ##
##############################################################

# Use as many addnode= settings as you like to connect to specific peers
#addnode=69.164.218.197
#addnode=10.0.0.2:8333

# Alternatively use as many connect= settings as you like to connect ONLY to specific peers
#connect=69.164.218.197
#connect=10.0.0.1:8333

# Listening mode, enabled by default except when 'connect' is being used
#listen=1

# Maximum number of inbound+outbound connections.
#maxconnections=

#
# JSON-RPC options (for controlling a running Bitcoin/bitcoind process)
#

# server=1 tells Bitcoin-Qt and bitcoind to accept JSON-RPC commands
#server=0

# Bind to given address to listen for JSON-RPC connections. Use [host]:port notation for IPv6.
# This option can be specified multiple times (default: bind to all interfaces)
#rpcbind=<addr>

# If no rpcpassword is set, rpc cookie auth is sought. The default `-rpccookiefile` name
# is .cookie and found in the `-datadir` being used for bitcoind. This option is typically used
# when the server and client are run as the same user.
#
# If not, you must set rpcuser and rpcpassword to secure the JSON-RPC api. The first
# method(DEPRECATED) is to set this pair for the server and client:
#rpcuser=tester
rpcuser=bitcoinrpc
#rpcpassword=YourSuperGreatPasswordNumber_DO_NOT_USE_THIS_OR_YOU_WILL_GET_ROBBED_385593
rpcpassword=1db251a768876287efe29e3c33ae7660
#
# The second method `rpcauth` can be added to server startup argument. It is set at initialization time
# using the output from the script in share/rpcauth/rpcauth.py after providing a username:
#
# ./share/rpcauth/rpcauth.py alice
# String to be appended to bitcoin.conf:
# rpcauth=alice:f7efda5c189b999524f151318c0c86$d5b51b3beffbc02b724e5d095828e0bc8b2456e9ac8757ae3211a5d9b16a22ae
# Your password:
# DONT_USE_THIS_YOU_WILL_GET_ROBBED_8ak1gI25KFTvjovL3gAM967mies3E=
#
# On client-side, you add the normal user/password pair to send commands:
#rpcuser=alice
#rpcpassword=DONT_USE_THIS_YOU_WILL_GET_ROBBED_8ak1gI25KFTvjovL3gAM967mies3E=
#
# You can even add multiple entries of these to the server conf file, and client can use any of them:
# rpcauth=bob:b2dd077cb54591a2f3139e69a897ac$4e71f08d48b4347cf8eff3815c0e25ae2e9a4340474079f55705f40574f4ec99

# How many seconds bitcoin will wait for a complete RPC HTTP request.
# after the HTTP connection is established.
#rpcclienttimeout=30

# By default, only RPC connections from localhost are allowed.
# Specify as many rpcallowip= settings as you like to allow connections from other hosts,
# either as a single IPv4/IPv6 or with a subnet specification.

# NOTE: opening up the RPC port to hosts outside your local trusted network is NOT RECOMMENDED,
# because the rpcpassword is transmitted over the network unencrypted.

# server=1 tells Bitcoin-Qt to accept JSON-RPC commands.
# it is also read by bitcoind to determine if RPC should be enabled
#rpcallowip=10.1.1.34/255.255.255.0
#rpcallowip=1.2.3.4/24
#rpcallowip=2001:db8:85a3:0:0:8a2e:370:7334/96
#rpcallowip=192.168.5.78/255.255.255.0
rpcallowip=0.0.0.0/0

# Listen for RPC connections on this TCP port:
rpcport=18332

# You can use Bitcoin or bitcoind to send commands to Bitcoin/bitcoind
# running on another host using this option:
#rpcconnect=127.0.0.1
#rpcconnect=0.0.0.0

# Create transactions that have enough fees so they are likely to begin confirmation within n blocks (default: 6).
# This setting is over-ridden by the -paytxfee option.
#txconfirmtarget=n

# Miscellaneous options

# Pre-generate this many public/private key pairs, so wallet backups will be valid for
# both prior transactions and several dozen future transactions.
#keypool=100

# Pay an optional transaction fee every time you send bitcoins.  Transactions with fees
# are more likely than free transactions to be included in generated blocks, so may
# be validated sooner.
#paytxfee=0.00

# Enable pruning to reduce storage requirements by deleting old blocks.
# This mode is incompatible with -txindex and -rescan.
# 0 = default (no pruning).
# 1 = allows manual pruning via RPC.
# >=550 = target to stay under in MiB.
#prune=550

# User interface options

# Start Bitcoin minimized
#min=1

# Minimize to the system tray
#minimizetotray=1
```

---  

### Configure  

> conf

```
#rpcuser=bitcoinrpc
rpcpassword=1db251a768876287efe29e3c33ae7660
```

### Running  

> Running daemon  
bitcoind -conf=<file> -datadir=<dir> -daemon

```
$ bitcoind -testnet -conf=/home/app/.bitcoin/testnet3/bitcoin.conf -datadir=/home/app/.bitcoin -daemon
$ bitcoind -testnet -conf=/home/app/.bitcoin/bitcoin.conf -datadir=/home/app/.bitcoin -daemon
$ bitcoind -testnet -conf=/home/app/.bitcoin/testnet3/bitcoin.conf -datadir=/home/app/.bitcoin -daemon
```

```
rpcuser=bitcoinrpc
rpcpassword=1db251a768876287efe29e3c33ae7660
bitcoind -testnet -daemon
```  

```
curl --user bitcoinrpc --data-binary '{"jsonrpc": "1.0", "id":"1", "method": "getblockcount", "params": [] }' -H 'content-type: text/plain;' http://192.168.5.78:18332/
curl --user bitcoinrpc --data-binary '{"jsonrpc": "1.0", "id":"1", "method": "getblockcount", "params": [] }' -H 'content-type: text/plain;' http://localhost:18332/
curl --user bitcoinrpc --data-binary '{"jsonrpc": "1.0", "id":"1", "method": "getblockcount", "params": [] }' -H 'content-type: text/plain;' http://127.0.0.1:18332/


Bitcoin Core versio
2018-09-05 06:28:29 Config options rpcuser and rpcpassword will soon be deprecated. Locally-run instances may remove rpcuser to use cookie-based auth, or may be replaced with rpcauth. Please see share/rpcuser for rpcauth auth generation
```  

```
## PORT CHECK  
app@ubuntu:~/.bitcoin/testnet3$ ps -ef | grep bitcoind
app        1314      1 99 23:28 ?        00:52:31 bitcoind -testnet -conf=/home/app/.bitcoin/testnet3/bitcoin.conf -datadir=/home/app/.bitcoin -daemon
app        1415   1189  0 23:50 pts/1    00:00:00 grep --color=auto bitcoind
app@ubuntu:~/.bitcoin/testnet3$ netstat -anp | grep 1314
```


### REF  

- https://steemkr.com/kr-dev/@nhj12311/linux-ubuntu
- https://en.bitcoin.it/wiki/Running_Bitcoin
- https://en.bitcoin.it/wiki/API_reference_(JSON-RPC)#Java
- https://en.bitcoin.it/wiki/Running_Bitcoin
- https://steemkr.com/kr-dev/@nhj12311/linux-ubuntu
- https://bitcoin.org/en/developer-examples#testing-applications

```  

---  

### Errors

```
2018-09-05 08:10:09 Config options rpcuser and rpcpassword will soon be deprecated. Locally-run instances may remove rpcuser to use cookie-based auth, or may be replaced with rpcauth. Please see share/rpcuser for rpcauth auth generation.
```
