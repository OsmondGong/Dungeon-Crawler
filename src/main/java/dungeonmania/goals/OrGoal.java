package dungeonmania.goals;

public class OrGoal implements GoalNode {

    private GoalNode b1;
    private GoalNode b2;

    public OrGoal(GoalNode b1, GoalNode b2) {
        this.b1 = b1;
        this.b2 = b2;
    }

    /**
     * @return Boolean
     */
    @Override
    public Boolean evaluate() {
        return b1.evaluate() || b2.evaluate();
    }

    /**
     * @return String
     */
    public String toString() {
        return "( " + b1.toString() + " OR " + b2.toString() + ")";
    }
}
