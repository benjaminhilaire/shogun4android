# Goal #

The goal of the next DEV will be to reduce the usefull board information from 3 SparseIntArray to a single Array();


# Details #

The information of the pawn are the following :

- Color (black/white)

- Shogun (shogun/warrior)

- Move possible (1,2,3,4)

We can use 4 bites to describe it (0>15)

1st bit : color (0 = black | 1 = white)

2nd bit : Shogun (0 = no| 1 = yes)

3&4 bites : Move (0 = 1 | 1 = 2 | 2 = 3 | 3 = 4 )

Using String temp = Integer.toBinaryString(tt); or any % operator we will easily convert from ID to pawn.

# Where (TODO) #

  * Replacing SparseIntArray by a single array
  * Replacing all pawn ID by this state
  * Replacing all pawn constructor and extract methods to use this state analyser
  * Using PAWN object as much as possible

# Conclusion #
> private SparseIntArray white;
> private SparseIntArray black;
> private SparseIntArray positions;
> private int pawnSelected = 0;
> private int posSelected = -1;
> private int whiteShogun;
> private int blackShogun;
> private boolean whiteTurn = true;

becomes

> private Integer posSelected = null;
> private boolean whiteTurn = true;
> SparseArray

&lt;Integer&gt;

 siTac = new SparseArray

&lt;Integer&gt;

(64);
> SparseBooleanArray possibleMoves = new SparseBooleanArray(64);
> private Boolean winnerWhite = null;

However, the "Integer" with null state meant a lot of NULL tests