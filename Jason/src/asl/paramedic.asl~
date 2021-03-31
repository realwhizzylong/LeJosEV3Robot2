// ========================================================================
// Initial beliefs and rules
// ========================================================================
// Determine the cost in assisting with the task.
// Truth is that we don't actually need this, but we will be using a variant
// of the CNP, which awards the contract to the agent with the lowest cost.
// In this assignment, only one agent will participate in the solution, and
// so the value generated here is somewhat arbitrary!
price(_Service,X) :- .random(R) & X = (10*R)+100.

// the name of the agent who is the initiator in the CNP
plays(initiator,doctor3).
//location(EV3,0,0).

// ========================================================================
// Initial goals
// ========================================================================
!update(slot).
// ========================================================================
// Plan Library
// ========================================================================
// Plans for the CNP
// send a message to the initiator introducing the agent as a participant 
+plays(initiator,In)
   :  .my_name(Me)
   <- .send(In,tell,introduction(participant,Me)).

// answer to Call For Proposal
@c1 +cfp(CNPId,Task,C,NC)[source(A)]
   :  plays(initiator,A) & price(Task,Offer)
   <- +proposal(CNPId,Task,C,NC,Offer);		// remember my proposal
      .send(A,tell,propose(CNPId,Offer)).

// Handling an Accept message
@r1 +accept_proposal(CNPId)[source(A)]
		: proposal(CNPId,Task,C,NC,Offer)
		<- !getScenario(A);
		    +startRescueMission(A,C,NC).
 
// Handling a Reject message
@r2 +reject_proposal(CNPId)
		<- .print("I lost CNP ",CNPId, ".");
		// clear memory
		-proposal(CNPId,_,_).

// ========================================================================
// Plan Library for beliefs and mental notes 
// ========================================================================
// Plan for responding to the creation of the new mental note
// startRescueMission(D,C,NC).  Although this is not technically
// modelled as an AgentSpeak intention (we have not covered how
// AgentSpeak manages BDI explicitly), this could be thought of
// as a new intention, and so the creation of the belief starts
// the mission!  This is the starting point of Assignment 2.
+startRescueMission(D,C,NC) : location(hospital,X,Y) & 
							  location(victim,_,_) &
							  location(obstacle,_,_)
    <- .count(location(victim,_,_),Vcount);		// Determine the victims
       .count(location(obstacle,_,_),Ocount);	// Determine the obstacles
       .print("Start the Resuce mission for ",C," critical and ",NC,
    		" non-critical victims; Hospital is at (",X,",",Y,"), and we have ",
    		Vcount, " victimes and ", Ocount," known obstacles").
 
+startRescueMission(D,C,NC)
    <- .wait(1000);  				
       -+startRescueMission(D,C,NC).

+location(victim,X,Y)[source(D)]: plays(initiator,D)
    <- .print("Victim could be at ",X,", ",Y); addVictim(X,Y).

+location(obstacle,X,Y)[source(D)]: plays(initiator,D)
    <- .print("Obstacle is at ",X,", ",Y); addObstacle(X,Y).
    
+location(hospital,X,Y)[source(D)]: plays(initiator,D)
    <- .print("Hospital is at ",X,", ",Y); addHospital(X,Y).
    
// ========================================================================
// Plan for responding to the critical status of victims at certain locations
// in the environment.  These are generated through communication with the
// doctor agents, via the achievement task +!requestVictimStatus(D,X,Y,C)

+critical(X,Y)
    <- .print("The victim at ", X, ",", Y, " is critical");
		tellEV3Result(1,X,Y).

+~critical(X,Y)
    <- .print("The victim at ", X, ",", Y, " is not critical");
		tellEV3Result(0,X,Y).

+searchFinished(A)
	<- +searchFinished(A);
		.print("The search is finished");
		changeRunMode(2).

+removePosition(X,Y,D)
	<- -location(victim,X,Y)[source(D)];
		changeRunMode(1).
	
+waitCommand(D,X,Y,C)
	<- .print("The EV3 robot is waitting for command");
		.send(D, tell, requestVictimStatus(X,Y,C)).

+pathFound(X,Y): true
	<- +pathFound(X,Y);
		.print("The goal point is (",X,",",Y,")").

+searchFinished(X,Y)
	<- .print("Search is finished").
	

// Plan Library for achievement goals 
+!update(slot)
	<- update;
		!update(slot).
+!update(slot).

+!getScenario(D) <- .send(D,askAll,location(_,_,_)).

// ========================================================================
// End 
// ========================================================================
    