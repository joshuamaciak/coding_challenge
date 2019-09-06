import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;


public class Parking {
    public static void main(final String... args) {
        if (args.length < 2) {
            System.out.println("usage: java Parking [initial] [target]");
            return;
        }

        final List<Integer> initial = Arrays.stream(args[0].split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        final List<Integer> target = Arrays.stream(args[1].split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        if (initial.size() != target.size()) {
            System.out.println("Initial & target states must be the same size");
            return;
        }

        if (!initial.contains(0) || !target.contains(0)) {
            System.out.println("Initial & target states must have one empty space");
        }

        PrintDirections(initial, target);
    }

    private static void PrintDirections(final List<Integer> initial, final List<Integer> target) {
        final LotState found = dfs(initial, target);
        if(found == null) {
            System.out.println("Couldn't find path from initial to target state (This should never happen)");
            return;
        }            
        System.out.println("Found solution");

        printLotDirections(found);
    }

    private static void printLotDirections(final LotState lotState) {
        if (lotState == null) {
            return;
        }
        printLotDirections(lotState.parent);
        if (lotState.move != null) {
            System.out.println(String.format("Move car from space %d to %d", lotState.move.space1, lotState.move.space2));
        }
    }

    private static LotState dfs(final List<Integer> initialState, final List<Integer> target) {
        final LotState needle = new LotState(target,null, null);
        final Set<LotState> visited = new HashSet<>();
        final Stack<LotState> stack = new Stack<>();
        stack.push(new LotState(initialState, null, null));

        while(!stack.isEmpty()) {
            final LotState state = stack.pop();
            visited.add(state);
            // compare states. if we're at the goal state we're done
            if (needle.equals(state)) {
                return state;
            }
            // else, expand the other states sans already visited
            final List<LotState> children = expandState(state, visited);
            for(LotState child : children) {
                stack.push(child);
            } 
        }
        return null;
    }

    /**
     * Just a wrapper class so we can conceptualize an abstract state rather than a list of spots
     */
    private static class LotState {
        private static final int EMPTY_SPOT = 0;
        private List<Integer> state;
        private LotState parent;
        private Move move;

        /**
         * A copy constructor to help keep enforce immutibility
         * @param lotState
         */
        LotState(final LotState lotState) {
            this.parent = lotState.parent;
            this.move = move;
            this.state = new ArrayList<>(lotState.state);
        }

        LotState(final List<Integer> state, final LotState parent, final Move move) {
            this.parent = parent;
            this.move = move;
            this.state = new ArrayList<>(state);
        }

        int getEmptySpot() {
            return state.indexOf(EMPTY_SPOT);
        }

        int getSpot(int car) {
            return state.indexOf(car);
        }

        int getNumSpots() {
            return this.state.size();
        }

        static LotState moveCarToEmptySpot(final LotState state, final int car) {
            final LotState newState = new LotState(state.state, state.parent, null);
            final int oldEmpty = newState.getEmptySpot();
            final int newEmpty = newState.getSpot(car);
            newState.state.set(newState.getEmptySpot(), car);
            newState.state.set(newEmpty, EMPTY_SPOT);
            newState.move = new Move(newEmpty, oldEmpty);

            return newState;
        }

        /**
         * NOTE: equals & hashcode only compare state. this should never
         * be done in production, but just trying to hack an algorithm here
        **/
        @Override
        public boolean equals(Object o) {
            final LotState that = (LotState) o;
            if (that == null) {
                return false;
            }
            return Objects.equals(this.state, that.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state);
        }

        @Override
        public String toString() {
            return this.state.toString();
        }
    }

    private static class Move {
        int space1;
        int space2;
        Move(int space1, int space2) {
            this.space1 = space1;
            this.space2 = space2;
        }
    }

    /**
     * Enumerates all of the potential states that could result from moving a car in the current
     * state. Excludes already visited states to avoid getting stuck in a loop.
     * @param state the lot state
     * @param visited a set of visited states
     * @return a list of unvisited child states
     */
    private static List<LotState> expandState(final LotState state, final Set<LotState> visited) {
        final List<LotState> unvisitedChildren = new ArrayList<>();
        final int emptySpot = state.getEmptySpot();

        for(int car = 1; car < state.getNumSpots(); ++car) {
            final LotState child = LotState.moveCarToEmptySpot(state, car);
            child.parent = state;
            if (!visited.contains(child)) {
                unvisitedChildren.add(child);
            } else {
            }
        }
        return unvisitedChildren;
    }

}
