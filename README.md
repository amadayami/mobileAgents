# Mobile Agents Project

Amadaya Michael and Nicholas Baggett

### Introduction

    This project creates a graph based off of an input file given the base station, the nodes, the edges, and the location
    the fire starts in the graph. It then uses threading in order to simulate a fire spreading.

    Distribution of work:

    Nicholas Baggett:

        Graphics
        Graph Data Structure
        Fire Spreading
        File Configuration
        Documentation

    Amadaya Michael:

        Creating General Structures to the Sensors and Agents
        Agents
        Documentation

### Usage

    In order to use this program, you must run the Graphics.java file in the command prompt / terminal with a command line argument
    for the configuration file. There are some configuration files within the resources directory.

    So, something like:

        java Graphics ../resources/test.txt

    Would work

### Project Assumptions

    There is only one fire, there is only one base station in the configuration file.

    The simulation ends when the base station catches on fire.

### Design Choice

    The Graph data structure we used was crucial to the program (see Graph.java file)

        This graph is created from the configuration file and holds the information for edges, nodes, and x and y positions
        for these nodes.

        The graph is used to hand information to the sensors and the agent in order to let the sensors know who their neighbors
        are, as they only have access to their neighbors.

        In addition, the graph is used in order to draw the simulation onto the screen. We used a search algorithm in order to
        draw the edges and nodes onto the screen. In the search algorithm, (see Display.java transverseNextNode() method)
        we recursively find neighboring nodes and draw them onto the board and mark them once they are found. In addition, when
        a neighbor is found a line is drawn between the two of the nodes. In addition, it makes for a check in order to find the base
        station when looping through the nodes of the graph and once it finds the base station will draw the log that the
        base station has been recording as it receives messages.
        This algorithm is called in an animation timer in order to display the simulation in real time.

        In addition, the Display class also uses a multiplier value in order to fit the graph and base log onto the screen
        no matter the given size or if it uses negative values for the nodes.

    The Fire Spreading:

        This algorithm is used within the Sensor class and uses threads and a BlockingQueue in order to give messages
        to neighboring sensors which can then be parsed and dealt with accordingly.

        If a sensor catches on fire, it sends an alert message to the neighboring sensors which then change their status
        to alert which is read into the Display class as a yellow node.

        Once the base station catches on fire, then it declares itself as dead and sends a message to all neighboring nodes
        kill themselves.

    The Agent Random Walk Algorithm and Duplication algorithm:

        EXPLAIN HERE

### Versions

    All versions submitted and their descriptions can be found in the commits section of the Git

### Docs

    The design document is in the docs directory.

### Known Issues

    Most of the known issues are most likely due to synchronization errors within our code.

    Issue FIRE:

        The fire spreading works perfectly fine by itself. You can see the jar file titled "Fire.jar" in order to see how
        this works by itself and that we know the cause of the break.

        However, when the agent is added into the code there seem to be synchronization issues that cause for the fire
        to not alert neighboring nodes when they are caught on fire OR for the nodes that received the alerting message to
        not handle this message quick enough or at all.

    Issue AGENTS:

        Duplication of the agents is inconsistent. We believe that the algorithm created to duplicate agents is mostly or
        completely correct, but there are synchronization errors that cause for this inconsistency.

        Sometimes, an agent is not duplicated from a child agent when the child is on a yellow node.

        Sometimes, an agent does not stop the random walk once it walks onto an already alerted (yellow) node.

        Sometimes, an agent is duplicated to a node several times as shown in the base station log.

    Issue BASE STATION:

        Sometimes, the messages will not reach the base station, but this is strictly due to the fact that a node will
        not process messages within its blocking queue. This occurs in the big_graph especially when the agent will reach
        the lower portion of the graph but then the fire will not spread to the base station.

        Because the fire does not spread to the base station, then the messages will not spread to the base station.

    In order to continue writing this code, or if we would have done something differently, we would have given more time
    on the synchronization portion of our code and would have probably worked together so we didn't have so many issues
    that occurred at once when we brought a lot of our code for the fire spreading and the agent logic together.

### Testing and Debugging

    Testing and debugging included running several tests on small portions of a code at a time in order to try to isolate the issues

    Eventually we ran out of time and had to move on with the program with current issues listed above.
