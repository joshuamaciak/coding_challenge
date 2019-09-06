# Algorithm Submission

## Building & running
Build

`javac Parking.java`

Run

`java Parking [initial] [target]`

ie) `java Parking 0,1,3,2,4 4,0,1,3,2`

Where `[initial]` & `[target]`  are comma separated strings of numbers.. Some trivial validation exists on input, but  is not robust since it is just a driver for the algorithm, it is important to ensure the in input is correct.

## Solution
After first digesting the problem, my gut feeling was that 
it was essentially a graph solving problem. We have a given configuration, and want to manipulate our state until we reach a goal state. With the restriction that there must be `n-1` cars in the lot at all times & each car can only move to the empty spot. The problem resembles a more general case of a Slide Puzzle. One important difference is that at each given state we have `n-1` possible moves rather that a fixed couple moves (max 4) in a slide puzzle. This distinction will become important later when we decide on a search strategy.

This type of problem can be cleanly represented by a tree of states. The root of the tree is our initial configuration. Children of a node
are the states that can be reached given one move. Another cool aspect of this is that each edge implicitly describes every move we make through the graph.

### Design
I decided to use a little bit of a higher level design in building this algorithm. For example, I abstract away the states to a `LotState` & build out some helper methods.
This clearly isn't the most efficient way in terms of memory or runtime, but when reasoning about a problem like this, I prefer to think more abstractly. There are more significant runtime gains by devising a better algorithm, plus it can always be optimized later. The runtime of both dfs & bfs is `O(|V| + |E|)`; however this is in the worst case so one may perform better based on the domain.  

One important thing to note is that we want to prevent getting stuck by visiting the same state over & over again, so we keep a set of our visited states.

### First attempt - BFS `debcf61`
Since finding a solution can be generalized into finding a path from the root to the goal state, the problem essentially becomes choosing an algorithm that executes the search efficiently.

BFS visits each node in a level before moving on to the next. One benefit of this is that we are guaranteed to find the shortest path to the goal state. This is helpful if we're trying to save the valet some time, but that's not the case. The big problem with using BFS is that it really struggles with trees with a high branching factor. As mentioned earlier, each node has `n-1` possible states. It also requires a lot of memory, and in my testing performance was awful at `n=7` and beyond.

### Second attempt - DFS w/ iterative deepening
After building the BFS, my next idea was to use another familiar search algo, DFS. DFS explores deeper into a tree & backtracks when a solution isn't found. This performs better than searching every node at a level if the branching factor is high & the goal state is deeper in the tree. Additionally it doesn't have the memory requirements that bfs has.

One gotcha with dfs is that it can get stuck going down a bad path, potentially forever in a problem like this. To overcome this, we can incrementally perform the search with an increasing limit.

### If I had more time - Heuristics
If I had more time to solve this problem, I'd consider using some sort of heuristic function to potentially speed up the algorithm. For example,
we can define some function `g(x)` that assigns a value to each state. The algorithm could then employ a priority queue to expand the most promising node. This would work similar to `A*` although each node in this problem has a uniform edge cost. One such heuristic we could use is defining 'closeness' to the ideal configuration. A very simple example could add a point for each car in the correct position. Under the assumption that the most cars in the right spot is the closest to being done. We could also find a way to define distance from a car & its spot, but this README is starting to turn into a book.