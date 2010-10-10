package org.spiritsofts.shogun.board.ai.basic;

import java.util.List;

import org.spiritsofts.shogun.board.ai.ArtificialIntelligence;
import org.spiritsofts.shogun.board.model.Pawn;

import android.util.Log;
import android.util.SparseIntArray;

public class LevelOne implements ArtificialIntelligence {
	
	private int[] mGame;
	private SparseIntArray rate;
	
	public LevelOne (){
		rate = new SparseIntArray(64*64);
	}

	@Override
	public  int[] calculateNextMove(int[] game) {
		mGame = game;
		IAevaluation();
		humanEvaluation();
		int max = -99;
		int idMax = -1;
		for (int i=0;i<mGame.length;i++){
			int current = mGame[i];
			if (current > max){
				max = current;
				idMax = i;
			}
			Log.i("SHOGUN","id:"+i+"|max:"+current);
		}
		int[] result = new int[2];
		result[0] = idMax%64;
		result[1] = (int) Math.floor(idMax/64);
		return result;
	}
	
	private void humanEvaluation() {
		int[] game = mGame;
		for (int origin=0;origin<64;origin++){
			int pawnState = game[origin];
			if (pawnState >= 0){
				Pawn pawn = new Pawn(pawnState,origin);
				if (pawn.isWhite()){
					List<Integer> list = pawn.getPossibleMove(game,false);
					for (int destination : list) { // It is the possible move
						rate.put(concanatePos(origin,destination),evaluateHumanMove(destination,rate.get(origin)));
					}
				}				
			}
		}
	}
	
	private void IAevaluation(){
		int[] game = mGame;
		for (int origin=0;origin<64;origin++){
			int pawnState = game[origin];
			if (pawnState >= 0){
				Pawn pawn = new Pawn(pawnState,origin);
				if (!pawn.isWhite()){
					List<Integer> list = pawn.getPossibleMove(game,false);
					for (int destination : list) { // It is the possible move
						rate.put(concanatePos(origin,destination),evaluateAIMove(destination,rate.get(origin)));
					}
				}				
			}
		}
	}

	private int concanatePos(int origin,int destination) {
		return origin+64*destination;
	}
	
	/**
	* -20 : IA shogun will be taken (HUMAN MOVE METHOD)
	* -2 : a IA pawn will be taken (HUMAN MOVE METHOD)
	* +1 = IA will protect another pawn (OK)
	* +2 = A human pawn will be taken (OK)
	* +20 : Human shogun will be taken (OK)
	 * @param target
	 * @return
	 */
	private int evaluateAIMove(int target,int rate) {
		int[] game = mGame;
		// Is a foe can be taken
		int moveState = game[target];
		if (moveState >= 0){
			Pawn pawn = new Pawn(moveState,target);
			if (pawn.isWhite()){
				if (pawn.isShogun()){
					rate+=20;
				} else {
					rate+=2;
				}
			} else {
				if (!pawn.isShogun()){
					rate+=1;
				}
			}
		}
		return rate;
	}
	
	private int evaluateHumanMove(int target,int rate) {
		int[] game = mGame;
		// Is a foe can be taken
		int moveState = game[target];
		if (moveState >= 0){
			Pawn pawn = new Pawn(moveState,target);
			if (pawn.isWhite()){
				if (pawn.isShogun()){
					rate-=20;
				} else {
					rate-=2;
				}
			}
		}
		return rate;
	}

}