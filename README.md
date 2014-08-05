discemone-server
================

Discemone is a large scale interactive sculpture being built for Burning Man 2014.  It consists of a collection of 30' tall illuminated towers connected through an XBee network.  This repo contains the code responsible for coordinating various components of the system.  This includes sensor inputs from capacitive input devices.

Full project coverage is available on our [Facebook](https://www.facebook.com/seagrassProject/ "Facebook seaGrass Page") page.

The discemone-server is the Scalatra component of the central control mechanism.  This provides access for monitoring and control.  It uses the discemone actor system to interact with the various asynchronous I/O systems.  This includes: XBee radio, at least 3 Teensy 3.1s that are monitoring control surfaces and sound system control.