package org.spiritsofts.shogun.board.view;

import android.util.SparseIntArray;

public class Pawn {
	private static final int IMPOSSIBLE_MOVE = 99;

	private int m_id; // ID of the pawn CANNOT BE CHANGED

	private int m_nbMove; // Number of move for the pawn

	private int m_location; // Iconic location

	private boolean m_white; // CANNOT BE CHANGED

	private boolean m_shogun; // CANNOT BE CHANGED

	public Pawn(int id, int location) {
		m_id = id;
		populateFromId(id);
		m_location = location;
	}

	public Pawn(int id) {
		m_id = id;
		populateFromId(id);
	}
	
	private void populateFromId(int id){
		m_white = false;
		if (id > 10 && id < 19) { // White pawn
			m_white = true;
		} else if (id == 10){
			m_white = true;
			m_shogun = true;
		} else if (id == 20){
			m_shogun = true;
		}
	}
	
	public Pawn(int id,SparseIntArray black,SparseIntArray white) {
		m_id = id;
		populateFromId(id);
		if (m_white){
			m_nbMove = white.get(id-10);
		} else {
			m_nbMove = black.get(id-20);
		}
	}
	
	public Pawn(int id,int location,SparseIntArray black,SparseIntArray white) {
		m_id = id;
		populateFromId(id);
		m_location = location;
		if (m_white){
			m_nbMove = white.get(id-10);
		} else {
			m_nbMove = black.get(id-20);
		}
	}

	public int getId() {
		return m_id;
	}

	public int getNbMove() {
		return m_nbMove;
	}

	public void setNbMove(int nbMove) {
		this.m_nbMove = nbMove;
	}

	public int getLocation() {
		return m_location;
	}

	public void setLocation(int location) {
		this.m_location = location;
	}

	public boolean isWhite() {
		return m_white;
	}

	public boolean isShogun() {
		return m_shogun;
	}

	public int[] getPossibleMove(SparseIntArray positions){
		int[] result = new int[4];
		int nbMove = m_nbMove;
		int location = m_location;
		int leftDistance = m_location%8; 
		if (leftDistance+nbMove < 8){ // Right
			result[1] =  isPossibleMove(location,location + nbMove,true,false,positions);
		} else {
			result[1] = IMPOSSIBLE_MOVE;
		}
		if (leftDistance-nbMove >= 0){
			result[0] = isPossibleMove(location,location - nbMove,true,true,positions);
		} else {
			result[0] = IMPOSSIBLE_MOVE;
		}
		int bigMove = 8*nbMove;
		if (m_location-bigMove < 64){
			result[2] = isPossibleMove(location,location - bigMove,false,true,positions);
		} else {
			result[2] = IMPOSSIBLE_MOVE;
		}
		if (leftDistance+bigMove >= 0){
			result[3] = isPossibleMove(location,location + bigMove,false,false,positions);
		} else {
			result[3] = IMPOSSIBLE_MOVE;
		}
		return result;
	}
	
	public int isPossibleMove(int origin,int target,boolean horizontal,boolean topleft,SparseIntArray positions){
		boolean success=true;
		int delta;
		if (horizontal){
			delta = 1;
		} else {
			delta = 8;
		}
		if (topleft){
			delta = -delta;
		}
		int it=origin;
		while(it != target && success){
			it = it+delta;
			if (positions.get(it) != 0 && it!= target){
				success = false;
			}
		}
		if (success && isNotAFriend(positions.get(target))){
			return target;
		} else {
			return IMPOSSIBLE_MOVE;
		}	
	}
	
	private boolean isNotAFriend(int id){
		if (m_white){
			if (id >= 10 && id < 20){
				return false;
			}
		} else {
			if (id >= 20 && id < 30){
				return false;
			}
		}
		return true;
	}
	
	public int getColoredId(){
		if (m_white){
			return m_id-10;
		} else {
			return m_id-20;
		}
	}
	
	public int getNextNbMove(){
		int next = m_nbMove+1;
		if (next > 4){
			next = 1;
		}
		return next;
	}

}
