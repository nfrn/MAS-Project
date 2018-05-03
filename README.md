# MAS-Project

* Here we distinguish task agents which manage and control the mobile units, and resource agents which manage and control the static resources. 
* Feasibility ants are issued regularly by resource agents to autonomously travel the environment and distribute information about the paths that they have followed - i.e. leaving road signs towards their respective resource agents. 
* Exploration ants are sent regularly by task agents to autonomously explore the environment for paths that its task agent could follow. On every node, an exploration ant interrogates the resource agent at that node to find out about a potential scheduling of its task agent. When a path is found, the exploration ants report back to their issuing task agent. The task agent weighs the different alternative paths, and decides upon one path as its intention. 
* Intention ants are regularly sent out to disseminate this information and make reservations at every node on the intended path. The terminology makes the link to BDI-based agents (Beliefs-Desires-Intentions, see [11][12]) obvious. 
* Exploration ants inform the task agent about possible options, after which a task agent selects an option as its intention.
