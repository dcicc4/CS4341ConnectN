To Run:
1. Place the AIPlayerDCiccarelli class in the Players package and the OurStateTree in the Utilities Package 
	so imports are correct and StateTreeDCiccarelli has access to package protected variables
2. Initialize the AIPlayerDciccarelli and Run
3. If program times out (due to us underestimating the performance of the computer running it),
	lower the maxDepth by 1 in the AIPlayerClass
	
	
Empirical Valuation:
	Our heuristic function is unusually as it considers streaks of empty spots and a player's
	pieces rather than just pieces in a row. The advantage to this method is that the value of
	a row 2110112 is more valuable than 201102. However the function needs to be tuned into how
	many empty spots between pieces are valuable ex. 21100112 should be less valued than 2110112.
	I needed a function to help determine how badly mulitple holes (empty spots between 2 of your
	own pieces) affect a streak's value. 
	
	First I determined a function to decide the relative values of the number of pieces in a streak. 
	I went with MAX_INT / (maxBoardSize - numberInStreak), as it distributes the number of pieces well. 
	For the hole correction I multiplied the initial value calculate above by k+(1/holes). 
	(The multiplier is not used if holes = 0). I needed to determine which k was the best adjusted for the
	weight of the holes so I cloned my AI and tried the hole values .1, .2, .3, and .4 as .5 would result 
	in 2 holes treated as the same as 1. I ran the 16 trials of all possible games and here are results 
			p1
		0.1	0.2	0.3	0.4
	0.1	p2	p2	p2	p2
p2	0.2	p2	p2	p1	p2
	0.3	p2	p2	p2	p1
	0.4	p2	p2	p2	p2
	
	Each spot is the winner of the round. So overall, .1 won 4/8, .2 3/8, .3 4/8, .4 5/8. While the 
	distribution of the results is very thin i expected it to be as much given that holes are not as
	large a factor as starting position. As a result of this experiment I set k = .4