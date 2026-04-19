package scr;
import java.util.ArrayList;
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
        ArrayList<String[]> dataBuku = FileHelper.bacaSemua(FileHelper.FILE_BUKU);

        if (dataBuku.isEmpty()) {
            System.out.println("\nBelum ada data buku.");
            return;
        }

        System.out.println("\n--- DAFTAR BUKU (" + dataBuku.size() + " judul) ---");
        System.out.println("No   Kode       Judul                          Jenis              Status");
        System.out.println("----------------------------------------------------------------------------");

        int nomor = 1;
        for (String[] baris : dataBuku) {
            if (baris.length >= 3) {
                // Cek apakah buku ini sedang dipinjam
                String status = FileHelper.apakahBukuDipinjam(baris[0]) ? "Dipinjam" : "Tersedia";
                System.out.printf("%-5d%-11s%-31s%-19s%s%n",
                    nomor, baris[0], baris[1], baris[2], status);
                nomor++;
            }
        }
    }

    // Menambah buku baruu
    static void tambahBuku(Scanner input) {
        System.out.println("\n--- TAMBAH BUKU BARU ---");

        System.out.print("Kode Buku  : ");
        String kode = input.nextLine().trim().toUpperCase();

        if (kode.isEmpty()) {
            System.out.println("Kode buku tidak boleh kosong.");
            return;
        }

        if (FileHelper.cariBuku(kode) != null) {
            System.out.println("Kode buku " + kode + " sudah ada.");
            return;
        }

        System.out.print("Judul      : ");
        String judul = input.nextLine().trim();

        if (judul.isEmpty()) {
            System.out.println("Judul tidak boleh kosong.");
            return;
        }

        System.out.print("Jenis Buku : ");
        String jenis = input.nextLine().trim();

        // Simpan ke file: KodeBuku|Judul|JenisBuku
        FileHelper.tambahBaris(FileHelper.FILE_BUKU, new String[]{kode, judul, jenis});
        System.out.println("Buku \"" + judul + "\" berhasil ditambahkan.");
    }

    // Mengedit data buku
    static void editBuku(Scanner input) {
        System.out.println("\n--- EDIT DATA BUKU ---");
        System.out.print("Masukkan Kode Buku yang ingin diedit: ");
        String kode = input.nextLine().trim().toUpperCase();

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_BUKU);
        boolean ketemu = false;

        for (String[] baris : semuaData) {
            if (baris.length >= 3 && baris[0].equalsIgnoreCase(kode)) {
                System.out.println("Data lama -> Judul: " + baris[1] + " | Jenis: " + baris[2]);
                System.out.println("(Kosongkan dan tekan Enter jika tidak ingin mengubah)");

                System.out.print("Judul baru : ");
                String judulBaru = input.nextLine().trim();

                System.out.print("Jenis baru : ");
                String jenisBaru = input.nextLine().trim();

                if (!judulBaru.isEmpty()) baris[1] = judulBaru;
                if (!jenisBaru.isEmpty()) baris[2] = jenisBaru;

                ketemu = true;
                break;
            }
        }

        if (ketemu) {
            FileHelper.tulisUlang(FileHelper.FILE_BUKU, semuaData);
            System.out.println("Data buku berhasil diperbarui.");
        } else {
            System.out.println("Buku dengan kode " + kode + " tidak ditemukan.");
        }
    }

    // Menghapus buku
    static void hapusBuku(Scanner input) {
        System.out.println("\n--- HAPUS BUKU ---");
        System.out.print("Masukkan Kode Buku yang ingin dihapus: ");
        String kode = input.nextLine().trim().toUpperCase();

        // Buku yang sedang dipinjam tidak bisa dihapus
        if (FileHelper.apakahBukuDipinjam(kode)) {
            System.out.println("Tidak bisa dihapus. Buku sedang dalam peminjaman.");
            return;
        }

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_BUKU);
        ArrayList<String[]> dataBaru  = new ArrayList<>();
        boolean ketemu = false;

        for (String[] baris : semuaData) {
            if (baris.length >= 1 && baris[0].equalsIgnoreCase(kode)) {
                ketemu = true;
            } else {
                dataBaru.add(baris);
            }
        }

        if (ketemu) {
            FileHelper.tulisUlang(FileHelper.FILE_BUKU, dataBaru);
            System.out.println("Buku berhasil dihapus.");
        } else {
            System.out.println("Buku dengan kode " + kode + " tidak ditemukan.");
        }
    }

    // Mencari buku berdasarkan kata kunci (judul atau jenis)
    static void cariBuku(Scanner input) {
        System.out.println("\n--- CARI BUKU ---");
        System.out.print("Masukkan kata kunci (judul/jenis): ");
        String kataKunci = input.nextLine().trim().toLowerCase();

        ArrayList<String[]> semuaBuku = FileHelper.bacaSemua(FileHelper.FILE_BUKU);
        boolean adaHasil = false;

        System.out.println("Kode       Judul                          Jenis              Status");
        System.out.println("-------------------------------------------------------------------");

        for (String[] baris : semuaBuku) {
            if (baris.length >= 3) {
                // Cek apakah kata kunci ada di judul atau jenis 
                boolean cocok = baris[1].toLowerCase().contains(kataKunci)
                             || baris[2].toLowerCase().contains(kataKunci);

                if (cocok) {
                    String status = FileHelper.apakahBukuDipinjam(baris[0]) ? "Dipinjam" : "Tersedia";
                    System.out.printf("%-11s%-31s%-19s%s%n", baris[0], baris[1], baris[2], status);
                    adaHasil = true;
                }
            }
        }

        if (!adaHasil) {
            System.out.println("Tidak ada buku dengan kata kunci \"" + kataKunci + "\".");
        }
    }
}
