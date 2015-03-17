# Introduction #

The IA level 1 is the stupidest level. It correspond to 1 move forecast.

It only try to TAKE a opponent pawn and avoid stupid moves.


# IA Basic #

For every "moveable" position, we RATE the move.
The highest RATE is the actual move the IA will do.

The base of the rating is the consequences (for n moves) of this move or stay :
  * -20 : IA shogun will be taken
  * -3 : A Human pawn won't be taken if it's move
  * -2 : a IA pawn will be taken
  * 1 : stop protecting another pawn
  * 0 : Neutral move
  * +1 = IA will protect another pawn
  * +2 = A human pawn will be taken
  * +3 = The pawn won't be taken if its moves
  * +20 : Human shogun will be taken

Each move will then have a RATE between -10 and 10.

# Level 1 analyse #

For the level 1, 1 move will be analized.
1 move means that both IA and Human moved.

# Complexity #

They are between 1 to 8 pawn to move.
Each pawn can move in 0 directions to 4.
The number of move position are between 1 and 32.

Then, the opponent can only make 32 moves. It means that basically, between 2 and 1024 different scenario need to be analyzed.

# Analyse #

The analyse will occurs in two times.

  * IA turn
  * Human turn

## IA Turn ##
For the IA turn, the only goal of the IA is to take an opponent pawn and avoid been taken.
If it can take a human pawn, it gains 2. If it can avoid been taken it gains 3.
Then if it can protect another IA pawn, it gains 1. If it stops protecting one, loose -1

## Human Turn ##
For the human turn the goal is to analyse if the human can take one of our pawn.
If the human can take one of yours, it loses 3.