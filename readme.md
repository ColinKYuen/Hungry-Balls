### SFU CMPT371 E100 Summer 2022 
#### Shervin Shirmohammadi
## Group 10 Final Project: Hungryballs

---

Created by Group 10:

| Member Name          | SFU ID          |
|----------------------|-----------------|
| Perapat Arerassadorn | parerass@sfu.ca |
| Darren Jennedy       | djennedy@sfu.ca |
| Shintaro Morikawa    | sma213@sfu.ca   |
| Gaelan O'Shea-McKay  | gosheamc@sfu.ca |
| Colin Yuen           | cya107@sfu.ca   |

---
## Hungryballs

Hungryballs is a multiplayer food-eating game designed for two players (the **host** and the **client**).

Players navigate an 8x8 grid, touching food to eat it. The first player to eat **5** pieces of food wins the game!

A demo video of the game running can be found [here](https://1sfu-my.sharepoint.com/:v:/g/personal/parerass_sfu_ca/EYmGbEY-B4dPt2Lw0grZ3usB3-50WSCuYTHQmjkTh7mkcw?e=PKQlDF).

---
### Running the App

Once the app is built, both the **host** and the **client** must run the app.

#### Host

To start a new server and host a game: `java Launcher <port>`

#### Client

To join an existing server and play: `java Launcher <host_addr> <port>`

Once a **client** has connected to a **host**, the game will begin.

---
### Playing the Game

Upon game start, players will automatically begin to move in their chosen **direction**.\
Players will continue to move in that direction unless they collide with a wall or another\
player or receive a new direction input.

**Change directions:** `W`, `A`, `S`, `D` or arrow keys `^`, `<`, `v`, `>`

The game will run until a player eats **5** pieces of food or one of the players **quits**.

**Quit the game:** `Q` or `Esc`

