package src;
import java.util.Scanner;

public class Main {

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        tampilkanJudul();

        // Buat akun default jika belum ada data pegawai sama sekali
        FileHelper.setupAwal();

        // Berikan 3 kali kesempatan login
        boolean loginBerhasil = false;
        int sisaPercobaan = 3;

        while (sisaPercobaan > 0) {
            loginBerhasil = proseLogin();
            if (loginBerhasil) break;

            sisaPercobaan--;
            if (sisaPercobaan > 0) {
                System.out.println("  Login gagal! Sisa percobaan: " + sisaPercobaan);
            }
        }

        if (!loginBerhasil) {
            System.out.println("\nTerlalu banyak percobaan login. Program dihentikan.");
            return;
        }

        // Masuk ke menu utama setelah login berhasil
        menuUtama();

        System.out.println("\nTerima kasih. Sampai jumpa!");
        input.close();
    }

    // Menampilkan judul/header program
    static void tampilkanJudul() {
        System.out.println("==========================================");
        System.out.println("     SISTEM PERPUSTAKAAN SMP             ");
        System.out.println("   Data Siswa, Buku & Peminjaman         ");
        System.out.println("==========================================");
        System.out.println();
    }

    // Proses login: minta NIP dan password, cocokkan dengan file pegawai
    static boolean proseLogin() {
        System.out.println("--- LOGIN PEGAWAI ---");
        System.out.print("NIP      : ");
        String nip = input.nextLine().trim();

        System.out.print("Password : ");
        String password = input.nextLine().trim();

        // Cari data pegawai di file berdasarkan NIP
        String[] dataPegawai = FileHelper.cariPegawai(nip);

        // Kolom password ada di indeks ke-3 (NIP|Nama|TglLahir|Password)
        if (dataPegawai != null && dataPegawai.length >= 4 && dataPegawai[3].equals(password)) {
            System.out.println("\nSelamat datang, " + dataPegawai[1] + "!");
            return true;
        }

        System.out.println("NIP atau password salah.");
        return false;
    }

    // Menu utama: pilih modul yang ingin digunakan
    static void menuUtama() {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("             MENU UTAMA                  ");
            System.out.println("==========================================");
            System.out.println("  1. Data Siswa");
            System.out.println("  2. Data Buku");
            System.out.println("  3. Data Pegawai");
            System.out.println("  4. Transaksi Peminjaman & Pengembalian");
            System.out.println("  5. Laporan");
            System.out.println("  0. Keluar");
            System.out.println("==========================================");
            System.out.print("Pilih menu: ");
            String pilihan = input.nextLine().trim();

            switch (pilihan) {
                case "1": Siswa.menuSiswa(input);           break;
                case "2": Buku.menuBuku(input);             break;
                case "3": Pegawai.menuPegawai(input);       break;
                case "4": Transaksi.menuTransaksi(input);   break;
                case "5": Transaksi.menuLaporan(input);     break;
                case "0": return;
                default:  System.out.println("Pilihan tidak valid. Coba lagi.");
            }
        }
    }
}
