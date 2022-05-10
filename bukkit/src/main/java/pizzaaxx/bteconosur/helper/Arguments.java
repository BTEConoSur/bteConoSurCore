package pizzaaxx.bteconosur.helper;

public class Arguments {

    private final String[] arguments;
    private static final String SPACE = " ";
    private static final String EMPTY = "";

    public Arguments(String[] arguments) {
        this.arguments = arguments;
    }

    public String join() {
        return String.join(SPACE, arguments);
    }

    public String getArgument(int size) {
        if (size >= arguments.length) {
            return EMPTY;
        }
        return arguments[size];
    }

    public String contactArguments(int first) {
        return contactArguments(first, arguments.length);
    }

    public String contactArguments(int first, int last) {
        if (first > arguments.length || last > arguments.length) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < last; i++) {
            stringBuilder.append(arguments[i])
                    .append(SPACE);
        }

        return stringBuilder.toString();
    }

    public void deleteArgument(int index) {
        if (index > arguments.length) {
            return;
        }

        arguments[index] = EMPTY;
    }

    public static Arguments from(String[] arguments) {
        return new Arguments(arguments);
    }

}
