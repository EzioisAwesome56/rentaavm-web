# Rent-a-VM Web interface
## What even is this
My teacher tasked me with creating a system that would allow people to login and request a vm for word docs or something, also giving the option for a higher end virtual machine for a "payment".<br><br>

Bascically, this is the poor man's vps provider front end.

## How the fuck do I use this?
This app requires Java 11 and rethinkdb. It was built to run ontop of ProxMox Virtual Enviorment, but could probably be adapted to whatever else.

## Installation
Create a new user (It is not reccomended to run as root)<br>
Allow this new user to run ``sudo qm`` and ``sudo echo`` without being prompted for a password. You can do this by adding these 2 lines to your /etc/sudoers files with visudo<br>
```user host = (root) NOPASSWD: /usr/sbin/qm user host = (root) NOPASSWD: /usr/bin/echo```
Replace user with your username and host with the hostname of the machine you are running the web server on<br>
then just run the jar.