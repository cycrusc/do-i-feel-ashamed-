/**
 * All console styling lives in this class: the banner, bordered menus, boxes and messages.
 *
 * To change the look, edit only the border constants below. For plain ASCII (if your console shows
 * "?" instead of box lines), set H and V to "-" and "|" and every corner/junction to "+".
 */
public class ConsoleUI {

    // ---------- border characters ----------
    private static final String H  = "\u2550";   // horizontal line
    public static final String V   = "\u2551";   // vertical line (public so tables can print rows with it)
    private static final String TL = "\u2554";   // top-left corner
    private static final String TR = "\u2557";   // top-right corner
    private static final String BL = "\u255A";   // bottom-left corner
    private static final String BR = "\u255D";   // bottom-right corner
    private static final String ML = "\u2560";   // left junction
    private static final String MR = "\u2563";   // right junction

    private static final int WIDTH = 60;          // inner width of menus, boxes and tables

    private static final String[] BANNER = {
            "    $$$$$$$\\                                                              $$\\",
            "    $$  __$$\\                                                             $$ |",
            "    $$ |  $$ | $$$$$$\\   $$$$$$\\   $$$$$$$\\  $$$$$$\\   $$$$$$$\\  $$$$$$\\  $$ |",
            "    $$$$$$$  |$$  __$$\\ $$  __$$\\ $$  _____|$$  __$$\\ $$  __$$\\  \\____$$\\ $$ |",
            "    $$  ____/ $$$$$$$$ |$$ |  \\__|\\$$$$$$\\  $$ /  $$ |$$ |  $$ | $$$$$$$ |$$ |",
            "    $$ |      $$   ____|$$ |       \\____$$\\ $$ |  $$ |$$ |  $$ |$$  __$$ |$$ |",
            "    $$ |      \\$$$$$$$\\ $$ |      $$$$$$$  |\\$$$$$$  |$$ |  $$ |\\$$$$$$$ |$$ |",
            "    \\__|       \\_______|\\__|      \\_______/  \\______/ \\__|  \\__| \\_______|\\__|",
            "",
            " $$$$$$\\            $$\\ $$\\                       $$\\     $$\\",
            "$$  __$$\\           $$ |$$ |                      $$ |    \\__|",
            "$$ /  \\__| $$$$$$\\  $$ |$$ | $$$$$$\\   $$$$$$$\\ $$$$$$\\   $$\\  $$$$$$\\   $$$$$$$\\",
            "$$ |      $$  __$$\\ $$ |$$ |$$  __$$\\ $$  _____|\\_$$  _|  $$ |$$  __$$\\ $$  __$$\\",
            "$$ |      $$ /  $$ |$$ |$$ |$$$$$$$$ |$$ /        $$ |    $$ |$$ /  $$ |$$ |  $$ |",
            "$$ |  $$\\ $$ |  $$ |$$ |$$ |$$   ____|$$ |        $$ |$$\\ $$ |$$ |  $$ |$$ |  $$ |",
            "\\$$$$$$  |\\$$$$$$  |$$ |$$ |\\$$$$$$$\\ \\$$$$$$$\\   \\$$$$  |$$ |\\$$$$$$  |$$ |  $$ |",
            " \\______/  \\______/ \\__|\\__| \\_______| \\_______|   \\____/ \\__| \\______/ \\__|  \\__|"
    };

    // ---------- banner ----------
    public static void printBanner() {
        int w = 0;
        for (String line : BANNER) {
            if (line.length() > w) w = line.length();
        }
        System.out.println();
        for (String line : BANNER) {
            System.out.println(line);
        }
        System.out.println(repeat(H, w));
        System.out.println(center("I N V E N T O R Y   M A N A G E M E N T   S Y S T E M", w));
        System.out.println(repeat(H, w));
    }

    // ---------- pieces of a bordered box (menus, boxes and tables all use these) ----------
    /** Blank line, top border, centered title, then a divider line. */
    public static void printTop(String title) {
        System.out.println();
        System.out.println(TL + repeat(H, WIDTH) + TR);
        System.out.println(V + center(title, WIDTH) + V);
        System.out.println(ML + repeat(H, WIDTH) + MR);
    }

    public static void printDivider() {
        System.out.println(ML + repeat(H, WIDTH) + MR);
    }

    public static void printBottom() {
        System.out.println(BL + repeat(H, WIDTH) + BR);
    }

    // ---------- menu: options are numbered 1..n and "[0] Logout" is always added ----------
    public static void printMenu(String title, String[] options) {
        printTop(title);
        for (int i = 0; i < options.length; i++) {
            printLine("[" + (i + 1) + "] " + options[i]);
        }
        printLine("[0] Logout");
        printBottom();
    }

    // ---------- box with a title and one or more lines of text ----------
    public static void printBox(String title, String body) {
        printTop(title);
        String[] lines = body.split("\n");
        for (String line : lines) {
            printLine(line);
        }
        printBottom();
    }

    // ---------- one-line messages ----------
    public static void success(String msg) { System.out.println("  [OK] " + msg); }
    public static void error(String msg)   { System.out.println("  [X]  " + msg); }
    public static void warn(String msg)    { System.out.println("  [!]  " + msg); }
    public static void info(String msg)    { System.out.println("  [i]  " + msg); }

    // ---------- helpers ----------
    /** One left-aligned line inside the box, cut or padded to exactly WIDTH characters. */
    private static void printLine(String text) {
        text = "  " + text;
        if (text.length() > WIDTH) {
            text = text.substring(0, WIDTH);
        }
        System.out.println(V + text + repeat(" ", WIDTH - text.length()) + V);
    }

    private static String center(String text, int w) {
        if (text.length() >= w) {
            return text.substring(0, w);
        }
        int left = (w - text.length()) / 2;
        return repeat(" ", left) + text + repeat(" ", w - text.length() - left);
    }

    /** Returns s written count times (a plain loop instead of String.repeat). */
    private static String repeat(String s, int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += s;
        }
        return result;
    }
}