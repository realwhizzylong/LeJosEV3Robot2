// Environment code for project doctor3018

import jason.asSyntax.*;
import jason.environment.*;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.logging.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Vector;



public class ParamedicEnv extends Environment {
	
    public static final int GSize = 6; // The bay is a 6x6 grid
    public static final int HOSPITAL  = 8; // hospital code in grid model
    public static final int VICTIM  = 16; // victim code in grid model

    private Logger logger = Logger.getLogger("doctor2018."+ParamedicEnv.class.getName());
	private Network netIn;
	private PCServer ser;
	private NetworkOut net;
	private int xGoal=0;
	private int yGoal=0;
	private int waitMode;
    
    // Create objects for visualising the bay.  
    // This is based on the Cleaning Robots code.

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
		ser = new PCServer();
		netIn = new Network(200,ser);
		net = new NetworkOut(200,ser);
		
		netIn.start();
		//net.start();
		updatePercepts();	
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        try {
        	if (action.getFunctor().equals("addVictim")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                logger.info("adding victim at: "+x+","+y);
				PCServer.map[x][y].occupied = 2;
				net.initMap(2,x,y);
            } else if (action.getFunctor().equals("addObstacle")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                logger.info("adding obstacle at: "+x+","+y);
				PCServer.map[x][y].occupied = 1;
				net.initMap(1,x,y);
            } else if (action.getFunctor().equals("addHospital")) {
               int x = (int)((NumberTerm)action.getTerm(0)).solve();
               int y = (int)((NumberTerm)action.getTerm(1)).solve();
               logger.info("adding hospital at: "+x+","+y);
				PCServer.map[x][y].occupied = 3;
				net.initMap(3,x,y);
            }else if(action.getFunctor().equals("tellEV3Result")){
				int critical = (int)((NumberTerm)action.getTerm(0)).solve();
				int x = (int)((NumberTerm)action.getTerm(1)).solve();
				int y = (int)((NumberTerm)action.getTerm(2)).solve();
				if(critical == 0){
					PCServer.map[x][y].critical=1;
					ser.setMode(1);
					net.toldToDo(ser.getMode());
				}else{
					PCServer.map[x][y].critical=2;
					ser.setMode(3);
					net.toldToDo(ser.getMode());
				}
			}else if(action.getFunctor().equals("changeRunMode")){
				int i = (int)((NumberTerm)action.getTerm(0)).solve();
				ser.setMode(i);
				net.toldToDo(ser.getMode());
			}else if(action.getFunctor().equals("update")){
				updatePercepts();
			}
			else{
                logger.info("executing: "+action+", but not implemented!");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(200);
        } catch (Exception e) {}
        informAgsEnvironmentChanged();
        return true;    
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
	
	void updatePercepts(){
		clearPercepts();
		
		if(xGoal != PCServer.xGoal || yGoal != PCServer.yGoal){
			Literal goal = Literal.parseLiteral("pathFound("+PCServer.xGoal+","+PCServer.yGoal+")");
			addPercept(goal);
			xGoal = PCServer.xGoal;
			yGoal = PCServer.yGoal;
		}
//		if(PCServer.victimNumber == 0){
//			Literal searchFinished = Literal.parseLiteral("searchFinished(EV3)");
//			addPercept(searchFinished);
//		}
		if(PCServer.listenEV3){
			if(PCServer.map[PCServer.xPos][PCServer.yPos].occupied  == 2 && PCServer.map[PCServer.xPos][PCServer.yPos].critical == 2&& PCServer.victimNumber != 0){
				Literal wait = Literal.parseLiteral("waitCommand(doctor3"+","+PCServer.xPos+","+PCServer.yPos+","+"burgandy)");
				addPercept(wait);
			}else if(PCServer.map[PCServer.xPos][PCServer.yPos].occupied  == 2 && PCServer.map[PCServer.xPos][PCServer.yPos].critical == 1){
				if(PCServer.victimNumber == 0){
					net.toldToDo(3);
				}else{
					Literal wait = Literal.parseLiteral("waitCommand(doctor3"+","+PCServer.xPos+","+PCServer.yPos+","+"cyan)");
					addPercept(wait);
				}
			}else if(PCServer.map[PCServer.xPos][PCServer.yPos].occupied == 2 && PCServer.map[PCServer.xPos][PCServer.yPos].critical ==0&& PCServer.victimNumber != 0){
					Literal wait = Literal.parseLiteral("removePosition("+PCServer.xPos+","+PCServer.yPos+",doctor3)");
					addPercept(wait);
			}else if(PCServer.victimNumber == 0){
				Literal wait = Literal.parseLiteral("serachFinished("+PCServer.xPos+","+PCServer.yPos+")");
				addPercept(wait);
				net.toldToDo(2);
			}else{
				net.toldToDo(1);
			}
		}
	}
}
