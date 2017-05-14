package org.finomnis.minesweeper.controller.ai;

import java.util.Map;

public interface MinesweeperSolver {

	public boolean solve(MinesweeperProblem problem);
	
	public Map<Coord, Boolean> getResults();
	
	public float getChance(Coord coord);
	
}
