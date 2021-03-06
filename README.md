# KME3

Kotlin Mass Effect 3

![License](https://img.shields.io/github/license/jacobtread/KME3?style=for-the-badge)
[![Gradle Build](https://img.shields.io/github/workflow/status/jacobtread/KME3/gradle-build?style=for-the-badge)](https://github.com/jacobtread/KME3/actions/workflows/gradle.yml)
![Total Lines](https://img.shields.io/tokei/lines/github/jacobtread/KME3?style=for-the-badge)

KME3 is a custom private server for Mass Effect 3 emulating the functionatlity of the official EA servers
but allowing you to host your own closed off private server or ever a server for playing together over lan

So far this project has become a very well performing server and is constantly being improved and optimized in my free time.
There are some bugs which need to be ironed out but at the moment its a perfectly usable server

## Links
- [**Latest Release** - The latest server jar release](https://github.com/jacobtread/KME3/releases/latest) 
- [**Client Setup** - Setting up yourself to connect to KME3 servers](docs/SETUP_CLIENT.md)
- [**Hosting Setup** - Setting up yourself to host a KME3 servers](docs/SETUP_HOSTER.md)
- [**Configuration Reference** - Configuration file info and what each setting in it does](docs/CONFIG_REFERENCE.md)

## Connection requirements

In order to be able to connect to the server the following entry needs to be made in the client machine host file
gosredirector.ea.com and this needs to point to the IP address of the host
I plan on making a tool for doing this automatically

> Note: if you play other EA games such as Battlefield this may affect your ability to connect
> to the servers for those games, so you will need to remove this redirect before playing them

## Planned

- [ ] Web panel interface
    - This has been started and is at [https://github.com/jacobtread/KME3Web](https://github.com/jacobtread/KME3Web) but will be included by default
- [ ] Leaderboard support

## Credits

1. [https://github.com/PrivateServerEmulator/ME3PSE](https://github.com/PrivateServerEmulator/ME3PSE) Sourced certificate and many game assets from this project
2. [https://github.com/Erik-JS/masseffect-binkw32](https://github.com/Erik-JS/masseffect-binkw32) Game patcher
