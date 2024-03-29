# MAS-Project
Information:
* Here we distinguish task agents which manage and control the mobile units, and resource agents which manage and control the static resources. 
* Feasibility ants are issued regularly by resource agents to autonomously travel the environment and distribute information about the paths that they have followed - i.e. leaving road signs towards their respective resource agents. 
* Exploration ants are sent regularly by task agents to autonomously explore the environment for paths that its task agent could follow. On every node, an exploration ant interrogates the resource agent at that node to find out about a potential scheduling of its task agent. When a path is found, the exploration ants report back to their issuing task agent. The task agent weighs the different alternative paths, and decides upon one path as its intention. 
* Intention ants are regularly sent out to disseminate this information and make reservations at every node on the intended path. The terminology makes the link to BDI-based agents (Beliefs-Desires-Intentions, see [11][12]) obvious. 
* Exploration ants inform the task agent about possible options, after which a task agent selects an option as its intention.

Papers:
* https://pdfs.semanticscholar.org/1a1f/03618f6ef4b08b2e8f8a3aa2276c6b7580f5.pdf
* https://www.safaribooksonline.com/library/view/advances-in-artificial/9780123970411/B9780123970411000054/B9780123970411000054.xhtml
* https://pdfs.semanticscholar.org/4a42/b57e6c1b88c526228f3ca293b9a6249fc9ec.pdf

Example Ant MAS with RinSim:
* https://github.com/sebakerckhof/2102sam/tree/master/RinSim/example/src/main/java/rinde/sim/project/agent/state
