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
       getOutR(state);

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
    
    


    
    
}
