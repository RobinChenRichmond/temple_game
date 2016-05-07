package student;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import game.EscapeState;
import game.ExploreState;
import game.Explorer;
import game.Node;
import game.NodeStatus;


public class Tennessee extends Explorer {
    /** Get to the orb in as few steps as possible. Once you get there, 
     * you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather 
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb, 
     * it will count as a failure.
     * 
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * 
     * At every step, you know only your current tile's ID and the ID of all 
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles). 
     * 
     * In order to get information about the current state, use functions
     * currentLocation(), neighbors(), and distanceToOrb() in ExploreState.
     * You know you are standing on the orb when distanceToOrb() is 0.
     * 
     * Use function moveTo(long id) in ExploreState to move to a neighboring 
     * tile by its ID. Doing this will change state to reflect your new position.
     * 
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.*/
    @Override public void getOrb(ExploreState state) {
        //TODO : Get the orb
    	HashSet<Long> a = new HashSet<Long>();
        a.add(state.currentLocation());
    	dfs(state, a, state.currentLocation());
        return;
    }
    
    public void dfs(ExploreState state, HashSet<Long> set, long prevPos){
    	if(state.distanceToOrb() == 0){
    		return;
    	}
    	Collection<NodeStatus> n = state.neighbors();
    	for(NodeStatus p:n){
    		if(state.distanceToOrb() == 0){
    			return;
    		}
    		if(p.getDistanceToTarget() < state.distanceToOrb() && !set.contains(p.getId())){
    			set.add(p.getId());
    			long currentPos = state.currentLocation();
    			state.moveTo(p.getId());
    			dfs(state,set,currentPos);
    		}
    	}

    	for(NodeStatus current:n){
    		if(state.distanceToOrb() == 0){
	    		return;
	    	}
    		if(!set.contains(current.getId())){
    			set.add(current.getId());
    			long currentPos = state.currentLocation();
    			state.moveTo(current.getId());
    			dfs(state, set, currentPos);
    		}
    	}
    	if(state.distanceToOrb() == 0){
    		return;
    	} else {
    		state.moveTo(prevPos);
    	}
    }
    
    
    /** Get out the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS get out before time runs
     * out, and this should be prioritized above collecting gold.
     * 
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * currentNode() and getExit() will return Node objects of interest, and getNodes()
     * will return a collection of all nodes on the graph. 
     * 
     * Note that the cavern will collapse in the number of steps given by stepsRemaining(),
     * and for each step this number is decremented by the weight of the edge taken. You can use
     * stepsRemaining() to get the time still remaining, seizeGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * 
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * 
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold. For this reason, using 
     * Dijkstra's to plot the shortest path to the exit is a good starting solution. */
    public void getOut(EscapeState state) {
        //TODO: Escape from the cavern before time runs out
       if(state.currentNode().getTile().getGold()>0){
    	   state.seizeGold();
       }
       //getOutR(state);
       getOut2(state);

    }
    
    private void getOutR(EscapeState state){
    	Collection<Node> availableNodes = state.getNodes();
    	double weight = 0;
        Node bestOne = null;
        for(Node m:availableNodes){

     	    if(m.getTile().getGold() > 0){
     		   List<Node> bestWay = Paths.dijkstra(state.currentNode(), m);
     		   List<Node> returnWay = Paths.dijkstra(m, state.getExit());
     		   
     		   int totalGold = 0;
     		   int totalWeight = 0;
     		   int returnWeight = 0;
     		   
     		   for(int i = 1; i < bestWay.size(); i ++){
     			   totalGold += bestWay.get(i).getTile().getGold();
     			   totalWeight += bestWay.get(i-1).getEdge(bestWay.get(i)).length();
     		   }
     		   
     		   for(int j = 1; j < returnWay.size();j ++){
     			   returnWeight += returnWay.get(j-1).getEdge(returnWay.get(j)).length();
     		   }

     		   if(totalWeight+returnWeight<state.stepsRemaining()){
     			   double tempWeight = totalGold/totalWeight;
     			   if(tempWeight > 40 && totalWeight < 35){
     				   tempWeight += 10;
     			   }
         		   //System.out.println(tempWeight);
     			   if(tempWeight>weight){
         			   weight = tempWeight;
         			   bestOne = m;
         		   }
     		   }
     	   }
        }
        
        if(bestOne == null){
        	List<Node> a = Paths.dijkstra(state.currentNode(),state.getExit());
            a.remove(0);
            for(Node n:a){
            		state.moveTo(n);
            		if(n.getTile().getGold()>0){
            			state.seizeGold();
            		}
            }
        } else{
        	List<Node> a = Paths.dijkstra(state.currentNode(), bestOne);
            a.remove(0);
            for(Node n:a){
            		state.moveTo(n);
            		if(n.getTile().getGold()>0){
            			state.seizeGold();
            		}
            }
        	getOutR(state);
        }
    }
    
    private void getOut2(EscapeState state){
    	Collection<Node> allNodes = state.getNodes();
    	int counter = 1;
    	for(Node m:allNodes){
    		if(m.getTile().getGold() > 0){
    			counter ++;
    		}
    	}
    	long[] a = new long[counter];
    	System.out.println("COUNTER: " + counter);
    	a[0] = state.currentNode().getId();
    	
    	for(int i = 1; i < counter; i ++){
    		a[i] = 0;
    	}
    	int[][] matrix = new int[counter][counter];
    	for(Node n:allNodes){
    		if(n.getTile().getGold() > 0){
    			for(int j = 1; j < counter; j ++){
    				if(a[j] == 0){
    					a[j] = n.getId();
    					break;
    				}
    				
    			}
    		}
    	}
    	
    	for(int p = 0; p < counter; p++){
    		for(int q = 0; q < counter; q++){
    			Node np = null;
    			Node nq = null;
    			int totalL = 0;
    			for(Node s:allNodes){
    				if(s.getId() == a[p]){
    					np = s;
    				}
    				if(s.getId() == a[q]){
    					nq = s;
    				}
    			}
    			List<Node> shortestPath = Paths.dijkstra(np, nq);
    			for(int r = 1; r < shortestPath.size(); r ++){
      			   totalL += shortestPath.get(r-1).getEdge(shortestPath.get(r)).length();
      		    }
    			matrix[p][q] = totalL;
    		}
    	}
    	
    	HashSet<Long> usedPoints = new HashSet<Long>();
    	long[][] b = new long[counter+1][3];
    	b[0][0] = state.currentNode().getId();
    	b[0][1] = 10000;
    	b[0][2] = 0;
    	
    	b[counter][0] = state.getExit().getId();
    	b[counter][1] = 10000;
    	b[counter][2] = 0;
    	
    	int sumLength = 0;
    	usedPoints.add(a[0]);
    	
    	for(int x = 0; x < counter; x++){
    		int shortest = 10000;
    		int indexInA = 0;
    		
    		if(x != counter-1){
    			for(int index = 0; index < counter; index++){
    				if(a[index] == b[x][0]){
    					indexInA = index;
    				}
    			}
        		for(int y = 1; y < counter; y ++){	
        			if(!usedPoints.contains(a[y])){
        				
        				if(matrix[indexInA][y] < shortest && x!=y){
            				shortest = matrix[indexInA][y];
            				b[x+1][0] = a[y];
            			}
        			}
        		}
        		usedPoints.add(b[x+1][0]);
    		}

    		if(x > 0){
				Node p1 = null;
				int p1p2L = 0;
				Node p2 = null;
				int p2p3L = 0;
				Node p3 = null;
				int p1p3L = 0;
				for(Node point:allNodes){
					if(point.getId() == b[x-1][0]){
						p1 = point;
					}
					if(point.getId() == b[x][0]){
						p2 = point;
					}
					if(point.getId() == b[x+1][0]){
						p3 = point;
					}
				}
				List<Node> p1p2 = Paths.dijkstra(p1, p2);
				for(int i1 = 1; i1 < p1p2.size(); i1 ++){
	      			   p1p2L += p1p2.get(i1-1).getEdge(p1p2.get(i1)).length();
	      		   }
				List<Node> p2p3 = Paths.dijkstra(p2, p3);
				for(int i2 = 1; i2 < p2p3.size(); i2 ++){
	      			   p2p3L += p2p3.get(i2-1).getEdge(p2p3.get(i2)).length();
	      		   }
				List<Node> p1p3 = Paths.dijkstra(p1, p3);
				for(int i3 = 1; i3 < p1p3.size(); i3 ++){
	      			   p1p3L += p1p3.get(i3-1).getEdge(p1p3.get(i3)).length();
	      		   }
				int moreW = p1p2L + p2p3L - p1p3L;
				b[x][2] = moreW;
				b[x][1] = (p2.getTile().getGold()+2000)*10/(moreW+1);
				sumLength  = sumLength + p1p2L;
				//System.out.println("SUMLENGTH" + sumLength);
				if(x == counter-1){
					sumLength += p2p3L;
				}
			}
    		
    	}
    	//System.out.println("SUMLENGTH" + sumLength);

    	while(sumLength > state.stepsRemaining()-10){
    		long worstW = 1000000;
    		int removeIndex = 0;
    		for(int i = 1; i < counter; i ++){
    			if(b[i][1] < worstW && b[i][0] > 0){
    				worstW = b[i][1];
    				removeIndex = i;
    			}
    		}
    		
    		sumLength = (int) (sumLength - b[removeIndex][2]);
    		System.out.println(sumLength);
    		b[removeIndex][0] = -1;
    		b[removeIndex][1] = 10000;
    		b[removeIndex][2] = 0;
    		
    		//update points
    		long indexX = -1;
    		long indexLeft = -1;
    		long indexY = -1;
    		long indexRight = -1;
    		for(int i = removeIndex-1; i >= 0; i--){
    			if(b[i][0] > 0){
    				if(indexX > 0){
    					indexLeft = b[i][0];
    					break;
    				} else{
    					indexX = b[i][0];
    				}
    			}
    		}
    		if(indexX == 0){
    			indexX = state.currentNode().getId();
    		}
    		for(int j = removeIndex+1; j < counter+1; j ++){
    			if(b[j][0] > 0){
    				if(indexY > 0){
    					indexRight = b[j][0];
    					break;
    				} else{
    					indexY = b[j][0];
    				}
    			}
    		}
    		//System.out.println(indexLeft + " " + indexX + " " + indexY + " " + indexRight);
    		if(indexLeft > 0){
    			long[] updateX = getWeight(indexLeft,indexX,indexY,state);
    			for(int k = 0; k < counter+1; k ++){
        			if(b[k][0] == indexX){
        				b[k][1] = updateX[0];
        				b[k][2] = updateX[1];
        			}
        		}
    		}
    		if(indexRight > 0){
    			long[] updateY = getWeight(indexX,indexY,indexRight,state);		
        		for(int k = 0; k < counter+1; k ++){
        			if(b[k][0] == indexY){
        				b[k][1] = updateY[0];
        				b[k][2] = updateY[1];
        			}
        		}
    		}

    	}
    	
    	b[0][0] = -1;
    	
    	int counterB = 0;
    	for(int count = 0; count < counter+1; count ++){
    		//System.out.println(b[count][0] + " " + b[count][1] + " " + b[count][2]);
    		if(b[count][0] != 0){
    			counterB++;
    		}
    	}
    	//System.out.println(counterB);
    	
		for(int i = 1; i < counter+1; i ++){
			if(b[i][0] > 0 && b[i][0] != state.currentNode().getId()){
				for(Node aaa:allNodes){
					if(aaa.getId() == b[i][0]){
						b[i][0] = -1;
						List<Node> move = Paths.dijkstra(state.currentNode(), aaa);
						move.remove(0);
						for(Node n:move){
		            		state.moveTo(n);
		            		if(n.getTile().getGold()>0){
		            			state.seizeGold();
		            		}
						}
					}
				}
			}
		}
    	
    }

    private long[] getWeight(long id1, long id2, long id3, EscapeState state){
    	Node p1 = null;
		int p1p2L = 0;
		Node p2 = null;
		int p2p3L = 0;
		Node p3 = null;
		int p1p3L = 0;
		for(Node point:state.getNodes()){
			if(point.getId() == id1){
				p1 = point;
			}
			if(point.getId() == id2){
				p2 = point;
			}
			if(point.getId() == id3){
				p3 = point;
			}
		}
		List<Node> p1p2 = Paths.dijkstra(p1, p2);
		for(int i1 = 1; i1 < p1p2.size(); i1 ++){
  			   p1p2L += p1p2.get(i1-1).getEdge(p1p2.get(i1)).length();
  		   }
		List<Node> p2p3 = Paths.dijkstra(p2, p3);
		for(int i2 = 1; i2 < p2p3.size(); i2 ++){
  			   p2p3L += p2p3.get(i2-1).getEdge(p2p3.get(i2)).length();
  		   }
		List<Node> p1p3 = Paths.dijkstra(p1, p3);
		for(int i3 = 1; i3 < p1p3.size(); i3 ++){
  			   p1p3L += p1p3.get(i3-1).getEdge(p1p3.get(i3)).length();
  		   }
		int moreW = p1p2L + p2p3L - p1p3L;
		
		long[] result = new long[2];
		result[1] = moreW;
		result[0] = (p2.getTile().getGold()+2000)*10/(moreW+1);
		
		return result;
    }
    
    
}
