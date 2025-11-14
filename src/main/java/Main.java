public class Main {
    public static void main(String[] args) {

        byte[] arr1a = {10, 20, 30};
        byte[] arr1b = arr1a;
        System.out.println("arr1a == arr1b: " + (arr1a == arr1b));

        byte[] arr2a = {10, 20, 30};
        byte[] arr2b = {10, 20, 30};
        System.out.println("arr2a == arr2b: " + (arr2a == arr2b));

        byte[] arr3a = {1, 2, 3};
        byte[] arr3b = {4, 5, 6};

        arr3b[0] = arr3a[0];
        arr3b[1] = (byte) (arr3a[1] + 0);
        arr3b[2] = arr3a[2];

        System.out.print("arr3a: {");
        for (int i = 0; i < arr3a.length; i++) {
            System.out.print(arr3a[i] + (i < arr3a.length - 1 ? ", " : ""));
        }
        System.out.println("}");

        System.out.print("arr3b: {");
        for (int i = 0; i < arr3b.length; i++) {
            System.out.print(arr3b[i] + (i < arr3b.length - 1 ? ", " : ""));
        }
        System.out.println("}");

        System.out.println("arr3a == arr3b after change: " + (arr3a == arr3b));

        String str4a = new String(arr3a);
        String str4b = new String(arr3b);
        System.out.println("str4a == str4b: " + (str4a == str4b));
        System.out.println("str4a.equals(str4b): " + str4a.equals(str4b));

        boolean arraysAreEqual = true;
        if (arr3a.length != arr3b.length) {
            arraysAreEqual = false;
        } else {
            for (int i = 0; i < arr3a.length; i++) {
                if (arr3a[i] != arr3b[i]) {
                    arraysAreEqual = false;
                    break;
                }
            }
        }
        System.out.println("Elements are equal: " + arraysAreEqual);

        byte b6 = 50;
//        int result6 = b6 + 256;
//        byte b61 = b6 + 1;
//        System.out.println("b6 + 256 = " + b61);

        byte b7 = 10;
        int i7 = b7 + 256;
        System.out.println("i7 = " + i7);

        byte b8 = 50;
        int target = 100;
        int count = 0;

        System.out.println("Loop start (b8 = 50, target = 100, decrementing)...");
        while (b8 != target) {

            if (count++ > 300) {
                System.out.println("Loop stopped to avoid infinite run.");
                break;
            }

            b8--;
        }

        System.out.println("Loop won't finish naturally (50 < 100, going down).");
    }
}