import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class Parking {
    public static void main(final String... args) {
        final List<Integer> initial = Arrays.asList(0,4,2,5,1,3);
        final List<Integer> target = Arrays.asList(0,1,5,3,2,4);
        PrintDirections(initial, target);
    }

    private static void PrintDirections(final List<Integer> initial, final List<Integer> target) {
        final LotState found = bfs(initial, target);
        if(found == null) {
            System.out.println("Couldn't find path from initial to target state (This should never happen)");
            return;
        }
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
//        System.out.println(lotState);
    }

    private static LotState bfs(final List<Integer> initialState, final List<Integer> target) {
        final LotState needle = new LotState(target,null, null);
        final Set<LotState> visited = new HashSet<>();
        final Queue<LotState> frontier = new ArrayDeque<>();
        frontier.add(new LotState(initialState, null, null));

        while(!frontier.isEmpty()) {
            final LotState state = frontier.remove();
            visited.add(state);
            // compare states. if we're at the goal state we're done
            if (needle.equals(state)) {
                return state;
            }
            // else, expand the other states sans already visited
            final List<LotState> children = expandState(state, visited);
            frontier.addAll(children);
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

        @Override
        public boolean equals(Object o) {
            final LotState that = (LotState) o;
            if (that == null) {
                return false;
            }
            return that.state == this.state || (that.state != null && that.state.equals(this.state));
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
            }
        }
        return unvisitedChildren;
    }

}