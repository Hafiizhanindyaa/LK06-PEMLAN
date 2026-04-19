import java.util.Scanner;

public class Buku {

    public static void menuBuku(Scanner input) {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("           MENU DATA BUKU                ");
            System.out.println("==========================================");
            System.out.println("  1. Lihat Semua Buku");
            System.out.println("  2. Tambah Buku Baru");
            System.out.println("  3. Edit Data Buku");
            System.out.println("  4. Hapus Buku");
            System.out.println("  5. Cari Buku (berdasarkan judul/jenis)");
            System.out.println("  0. Kembali ke Menu Utama");
            System.out.println("==========================================");
            System.out.print("Pilih menu: ");
            String pilihan = input.nextLine().trim();

            switch (pilihan) {
                case "1": lihatSemuaBuku();     break;
                case "2": tambahBuku(input);    break;
                case "3": editBuku(input);      break;
                case "4": hapusBuku(input);     break;
                case "5": cariBuku(input);      break;
                case "0": return;
                default:  System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Menampilkan semua data buku beserta statusnya
    static void lihatSemuaBuku() {
        }

    // Menambah buku baruu
    static void tambahBuku(Scanner input) {
        }

    // Mengedit data buku
    static void editBuku(Scanner input) {
        }

    // Menghapus buku
    static void hapusBuku(Scanner input) {
        }

    // Mencari buku berdasarkan kata kunci (judul atau jenis)
    static void cariBuku(Scanner input) {
    }
}
