package org.spiritsofts.shogun.board.model;

import android.util.SparseArray;

public class Pawn {

	private int m_location; // Iconic location

	private Boolean m_white; // CANNOT BE CHANGED

	private Boolean m_shogun; // CANNOT BE CHANGED
	
	private Integer m_nbMove; // Number of move for the pawn
	
	private int m_state;

	public Pawn(Integer state,int location) {
		m_state = state;
		m_location = location;
	}
	
	public Pawn(Integer state) {
		m_state = state;
	}
	
	public boolean isWhite(){
		if (m_white == null){
			m_white = ((m_state % 2) == 1);
		}
		return m_white;
	}
	
	public int getNbMove() {
		if (m_nbMove == null){
			m_nbMove = (int) Math.floor(m_state / 4)+1;
		}
		return m_nbMove;
	}

	public int getLocation() {
		return m_location;
	}

	public void setLocation(int location) {
		this.m_location = location;
	}

	public boolean isShogun() {
		if (m_shogun == null){
			m_shogun = ((m_state / 2)%2 == 1);
		}
		return m_shogun;
	}

	public Integer[] getPossibleMove(SparseArray<Integer> siTac){
		Integer[] result = new Integer[4];
		int nbMove = getNbMove();
		int location = m_location;
		int leftDistance = m_location%8; 
		if (leftDistance-nbMove >= 0){ // LEFT
			result[0] = isPossibleMove(location,location - nbMove,true,true,siTac);
		}
		if (leftDistance+nbMove < 8){ // RIGHT
			result[1] =  isPossibleMove(location,location + nbMove,true,false,siTac);
		}
		int bigMove = 8*nbMove;
		if (m_location-bigMove < 64){ // TOP 
			result[2] = isPossibleMove(location,location - bigMove,false,true,siTac);
		}
		if (leftDistance+bigMove >= 0){ // BOTTOM
			result[3] = isPossibleMove(location,location + bigMove,false,false,siTac);
		}
		return result;
	}
	
	public Integer isPossibleMove(int origin,int target,boolean horizontal,boolean topleft,SparseArray<Integer> siTac){
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
			if (siTac.get(it) != null && it!= target){
				success = false;
			}
		}
		if (success && isNotAFriend(siTac.get(target))){
			return target;
		}
		return null;
	}
	
	private boolean isNotAFriend(Integer otherState){
		if (otherState != null){
			if (m_white){
				return (otherState % 2 == 0);
			} else {
				return (otherState % 2 == 1);
			}
		}
		return true;
	}
	
	public int getNextState(){
		int next = getNbMove();
		if (next >= 4){
			next = 0;
		}
		next*=4;
		if (isShogun()){
			next+=2;
		}
		if (isWhite()){
			next++;
		}
		return next;
	}

}
