package security;

public class SHA_1 {

    private static int rol(int num, int cnt) {
        return (num << cnt) | (num >>> (32 - cnt));
    }

    public static String encodeHex(String str) {

        /*
           -> Input an arbitrary length of string.
           i.e: "abc"

         */

        byte[] x = str.getBytes();

        /*
            -> Get an ASCII value of every character of inputed String.
            i.e: 97, 98, 99
         */

        int[] blks = new int[(((x.length + 8) >> 6) + 1) * 16];

        /*
            -> Convert it to binary and fill empty bit with zero.
            i.e: 01100001, 01100010, 01100011

            -> Make an int array with size 'l'.
            where 'l' is:

            n = inputedString.length()

            if(n%64) == 0
                then l' = (n/64)*64 or l' = n
            else l' = [(n/64) * 64] + 64

            now, l = l'/4

         */

        int i;

        for (i = 0; i < x.length; i++) {
            blks[i >> 2] |= x[i] << (24 - (i % 4) * 8);
        }

        blks[i >> 2] |= 0x80 << (24 - (i % 4) * 8);
        blks[blks.length - 1] = x.length * 8;

        /*
            -> Now append the binary format of inputed string to newly created int array
                and fill the empty bit with zero.
               i.e: 01100001 01100010 01100011 00000...000...000...000000

         */

        int[] w = new int[80];

        int a = 1732584193;
        int b = -271733879;
        int c = -1732584194;
        int d = 271733878;
        int e = -1009589776;

        /*
            -> create an int array of size 80 and initialize 5 constant -

            (a) H_0 = 1732584193
            (b) H_1 = -271733879
            (c) H_2 = -1732584194
            (d) H_3 = 271733878
            (e) H_4 = -1009589776

         */

        for (i = 0; i < blks.length; i += 16) {
            int olda = a;
            int oldb = b;
            int oldc = c;
            int oldd = d;
            int olde = e;

            for (int j = 0; j < 80; j++) {
                w[j] = (j < 16) ? blks[i + j] :
                        (rol(w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16], 1));

                /*
                    if(j < 16)
                        then w[j] = blks[i + j]
                    else
                        w[j] =  ( rol(w[j-3] ^ w[j-8] ^ w[j-14] ^ w[j-16], 1) )
                 */

                int t = rol(a, 5) + e + w[j] +
                        ((j < 20) ? 1518500249 + ((b & c) | ((~b) & d))
                                : (j < 40) ? 1859775393 + (b ^ c ^ d)
                                : (j < 60) ? -1894007588 + ((b & c) | (b & d) | (c & d))
                                : -899497514 + (b ^ c ^ d));

                /*
                    t = rol(a, 5) + e + w[j] + X
                    where 'X' is:

                    if(j<20)
                        then X = 1518500249 + ((b & c) | ((~b) & d))
                     else if(j<40)
                        then X =  1859775393 + (b ^ c ^ d)
                     else if(j<60)
                        then X =  -1894007588 + ((b & c) | (b & d) | (c & d))
                     else
                        X = -899497514 + (b ^ c ^ d) )

                 */

                e = d;
                d = c;
                c = rol(b, 30);
                b = a;
                a = t;
            }

            a = a + olda;
            b = b + oldb;
            c = c + oldc;
            d = d + oldd;
            e = e + olde;
        }

        /*
           -> Manipulate those 5 constant using following method with some iteration and condition
         */

        int[] words = {a, b, c, d, e};
        StringBuilder sb = new StringBuilder();

        for (int word : words) {
            String hexWord = Integer.toHexString(word);
            while (hexWord.length() < 8) {
                hexWord = "0" + hexWord;
            }
            sb.append(hexWord);
        }

        /*
            -> After manipulate those constant H_0 ... H_4
            -> Now take a Hexadecimal representation of them.
               i.e: H_0 = -1449574858
                    Hex(H_0) = a9993e36

         */

        return sb.toString();

        /*
            -> The output of message digest is always 160 bits/20bytes/5 int size.
            -> The total character of message digest is 40. Because each Hexa representation of
                 H_0...H_4 is 8 bits and total bits is 160.

         */
    }


}

