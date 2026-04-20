package src;

import java.util.ArrayList;
import java.util.Scanner;

public class Siswa {

    public static void menuSiswa(Scanner input) {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("           MENU DATA SISWA               ");
            System.out.println("==========================================");
            System.out.println("  1. Lihat Semua Siswa");
            System.out.println("  2. Tambah Siswa Baru");
            System.out.println("  3. Edit Data Siswa");
            System.out.println("  4. Hapus Siswa");
            System.out.println("  5. Cari Siswa (berdasarkan nama/NIS)");
            System.out.println("  0. Kembali ke Menu Utama");
            System.out.println("==========================================");
            System.out.print("Pilih menu: ");
            String pilihan = input.nextLine().trim();

            switch (pilihan) {
                case "1": lihatSemuaSiswa();    break;
                case "2": tambahSiswa(input);   break;
                case "3": editSiswa(input);     break;
                case "4": hapusSiswa(input);    break;
                case "5": cariSiswa(input);     break;
                case "0": return;
                default:  System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Menampilkan semua data siswa beserta status jumlah pinjaman aktifnya
    static void lihatSemuaSiswa() {
        ArrayList<String[]> dataSiswa = FileHelper.bacaSemua(FileHelper.FILE_SISWA);

        if (dataSiswa.isEmpty()) {
            System.out.println("\nBelum ada data siswa.");
            return;
        }

        System.out.println("\n--- DAFTAR SISWA (" + dataSiswa.size() + " orang) ---");
        System.out.println("No   NIS        Nama                           Alamat                  Pinjaman Aktif");
        System.out.println("-------------------------------------------------------------------------------------");

        int nomor = 1;
        for (String[] baris : dataSiswa) {
            if (baris.length >= 3) {
                // Mengecek berapa buku yang sedang dipinjam siswa ini
                int jumlahPinjam = FileHelper.hitungPinjamanAktif(baris[0]);
                String infoPinjaman = jumlahPinjam + " buku";
                
                System.out.printf("%-5d%-11s%-31s%-24s%s%n",
                    nomor, baris[0], baris[1], baris[2], infoPinjaman);
                nomor++;
            }
        }
    }

    // Menambah siswa baru
    static void tambahSiswa(Scanner input) {
        System.out.println("\n--- TAMBAH SISWA BARU ---");

        System.out.print("NIS Siswa  : ");
        String nis = input.nextLine().trim().toUpperCase();

        if (nis.isEmpty()) {
            System.out.println("NIS tidak boleh kosong.");
            return;
        }

        if (FileHelper.cariSiswa(nis) != null) {
            System.out.println("Siswa dengan NIS " + nis + " sudah terdaftar.");
            return;
        }

        System.out.print("Nama Siswa : ");
        String nama = input.nextLine().trim();

        if (nama.isEmpty()) {
            System.out.println("Nama tidak boleh kosong.");
            return;
        }

        System.out.print("Alamat     : ");
        String alamat = input.nextLine().trim();

        // Simpan ke file: NIS|Nama|Alamat
        FileHelper.tambahBaris(FileHelper.FILE_SISWA, new String[]{nis, nama, alamat});
        System.out.println("Siswa \"" + nama + "\" berhasil ditambahkan.");
    }

    // Mengedit data siswa
    static void editSiswa(Scanner input) {
        System.out.println("\n--- EDIT DATA SISWA ---");
        System.out.print("Masukkan NIS Siswa yang ingin diedit: ");
        String nis = input.nextLine().trim().toUpperCase();

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_SISWA);
        boolean ketemu = false;

        for (String[] baris : semuaData) {
            if (baris.length >= 3 && baris[0].equalsIgnoreCase(nis)) {
                System.out.println("Data lama -> Nama: " + baris[1] + " | Alamat: " + baris[2]);
                System.out.println("(Kosongkan dan tekan Enter jika tidak ingin mengubah)");

                System.out.print("Nama baru   : ");
                String namaBaru = input.nextLine().trim();

                System.out.print("Alamat baru : ");
                String alamatBaru = input.nextLine().trim();

                if (!namaBaru.isEmpty()) baris[1] = namaBaru;
                if (!alamatBaru.isEmpty()) baris[2] = alamatBaru;

                ketemu = true;
                break;
            }
        }

        if (ketemu) {
            FileHelper.tulisUlang(FileHelper.FILE_SISWA, semuaData);
            System.out.println("Data siswa berhasil diperbarui.");
        } else {
            System.out.println("Siswa dengan NIS " + nis + " tidak ditemukan.");
        }
    }

    // Menghapus siswa
    static void hapusSiswa(Scanner input) {
        System.out.println("\n--- HAPUS SISWA ---");
        System.out.print("Masukkan NIS Siswa yang ingin dihapus: ");
        String nis = input.nextLine().trim().toUpperCase();

        // LOGIKA PENTING: Siswa yang masih meminjam buku tidak boleh dihapus
        if (FileHelper.hitungPinjamanAktif(nis) > 0) {
            System.out.println("Gagal menghapus. Siswa ini masih memiliki buku yang belum dikembalikan.");
            return;
        }

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_SISWA);
        ArrayList<String[]> dataBaru  = new ArrayList<>();
        boolean ketemu = false;

        for (String[] baris : semuaData) {
            if (baris.length >= 1 && baris[0].equalsIgnoreCase(nis)) {
                ketemu = true;
            } else {
                dataBaru.add(baris);
            }
        }

        if (ketemu) {
            FileHelper.tulisUlang(FileHelper.FILE_SISWA, dataBaru);
            System.out.println("Data siswa berhasil dihapus.");
        } else {
            System.out.println("Siswa dengan NIS " + nis + " tidak ditemukan.");
        }
    }

    // Mencari siswa berdasarkan kata kunci (nama atau NIS)
    static void cariSiswa(Scanner input) {
        System.out.println("\n--- CARI SISWA ---");
        System.out.print("Masukkan kata kunci (NIS/Nama): ");
        String kataKunci = input.nextLine().trim().toLowerCase();

        ArrayList<String[]> semuaSiswa = FileHelper.bacaSemua(FileHelper.FILE_SISWA);
        boolean adaHasil = false;

        System.out.println("NIS        Nama                           Alamat                  Pinjaman Aktif");
        System.out.println("--------------------------------------------------------------------------------");

        for (String[] baris : semuaSiswa) {
            if (baris.length >= 3) {
                // Cek apakah kata kunci ada di NIS atau Nama
                boolean cocok = baris[0].toLowerCase().contains(kataKunci)
                             || baris[1].toLowerCase().contains(kataKunci);

                if (cocok) {
                    int jumlahPinjam = FileHelper.hitungPinjamanAktif(baris[0]);
                    String infoPinjaman = jumlahPinjam + " buku";
                    
                    System.out.printf("%-11s%-31s%-24s%s%n", baris[0], baris[1], baris[2], infoPinjaman);
                    adaHasil = true;
                }
            }
        }

        if (!adaHasil) {
            System.out.println("Tidak ada siswa dengan kata kunci \"" + kataKunci + "\".");
        }
    }
}
