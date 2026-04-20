package src;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Siswa
 * Menangani semua operasi data siswa: lihat, tambah, edit, hapus.
 * Format data di file siswa.txt: NIS|Nama|Alamat
 */
public class Siswa {

    // -------------------------------------------------------------------------
    // Menu pilihan untuk modul Siswa
    // -------------------------------------------------------------------------

    public static void menuSiswa(Scanner input) {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("           MENU DATA SISWA               ");
            System.out.println("==========================================");
            System.out.println("  1. Lihat Semua Siswa");
            System.out.println("  2. Tambah Siswa Baru");
            System.out.println("  3. Edit Data Siswa");
            System.out.println("  4. Hapus Siswa");
            System.out.println("  0. Kembali ke Menu Utama");
            System.out.println("==========================================");
            System.out.print("Pilih menu: ");
            String pilihan = input.nextLine().trim();

            switch (pilihan) {
                case "1": lihatSemuaSiswa();    break;
                case "2": tambahSiswa(input);   break;
                case "3": editSiswa(input);     break;
                case "4": hapusSiswa(input);    break;
                case "0": return;
                default:  System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Menampilkan semua data siswa dari file
    // -------------------------------------------------------------------------

    static void lihatSemuaSiswa() {
        ArrayList<String[]> dataSiswa = FileHelper.bacaSemua(FileHelper.FILE_SISWA);

        if (dataSiswa.isEmpty()) {
            System.out.println("\nBelum ada data siswa.");
            return;
        }

        System.out.println("\n--- DAFTAR SISWA (" + dataSiswa.size() + " orang) ---");
        System.out.println("No   NIS          Nama                      Alamat");
        System.out.println("-----------------------------------------------------------");

        int nomor = 1;
        for (String[] baris : dataSiswa) {
            if (baris.length >= 3) {
                // Cetak dengan format rapi
                System.out.printf("%-5d%-13s%-27s%s%n", nomor, baris[0], baris[1], baris[2]);
                nomor++;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Menambah siswa baru
    // -------------------------------------------------------------------------

    static void tambahSiswa(Scanner input) {
        System.out.println("\n--- TAMBAH SISWA BARU ---");

        System.out.print("NIS    : ");
        String nis = input.nextLine().trim().toUpperCase();

        // Validasi: NIS tidak boleh kosong
        if (nis.isEmpty()) {
            System.out.println("NIS tidak boleh kosong.");
            return;
        }

        // Validasi: NIS tidak boleh sama dengan yang sudah ada
        if (FileHelper.cariSiswa(nis) != null) {
            System.out.println("NIS " + nis + " sudah terdaftar.");
            return;
        }

        System.out.print("Nama   : ");
        String nama = input.nextLine().trim();

        if (nama.isEmpty()) {
            System.out.println("Nama tidak boleh kosong.");
            return;
        }

        System.out.print("Alamat : ");
        String alamat = input.nextLine().trim();

        // Simpan ke file: NIS|Nama|Alamat
        FileHelper.tambahBaris(FileHelper.FILE_SISWA, new String[]{nis, nama, alamat});
        System.out.println("Siswa " + nama + " berhasil ditambahkan.");
    }

    // -------------------------------------------------------------------------
    // Mengedit data siswa yang sudah ada
    // -------------------------------------------------------------------------

    static void editSiswa(Scanner input) {
        System.out.println("\n--- EDIT DATA SISWA ---");
        System.out.print("Masukkan NIS yang ingin diedit: ");
        String nis = input.nextLine().trim().toUpperCase();

        // Baca semua data dulu
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

                // Hanya update jika pengguna mengisi sesuatu
                if (!namaBaru.isEmpty()) baris[1] = namaBaru;
                if (!alamatBaru.isEmpty()) baris[2] = alamatBaru;

                ketemu = true;
                break;
            }
        }

        if (ketemu) {
            // Tulis ulang semua data ke file
            FileHelper.tulisUlang(FileHelper.FILE_SISWA, semuaData);
            System.out.println("Data siswa berhasil diperbarui.");
        } else {
            System.out.println("Siswa dengan NIS " + nis + " tidak ditemukan.");
        }
    }

    // -------------------------------------------------------------------------
    // Menghapus data siswa
    // -------------------------------------------------------------------------

    static void hapusSiswa(Scanner input) {
        System.out.println("\n--- HAPUS SISWA ---");
        System.out.print("Masukkan NIS yang ingin dihapus: ");
        String nis = input.nextLine().trim().toUpperCase();

        // Cek dulu: siswa ini masih punya pinjaman aktif?
        if (FileHelper.hitungPinjamanAktif(nis) > 0) {
            System.out.println("Tidak bisa dihapus. Siswa masih memiliki pinjaman buku yang belum dikembalikan.");
            return;
        }

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_SISWA);
        ArrayList<String[]> dataBaru  = new ArrayList<>();
        boolean ketemu = false;

        for (String[] baris : semuaData) {
            if (baris.length >= 1 && baris[0].equalsIgnoreCase(nis)) {
                ketemu = true;
                // Baris ini tidak dimasukkan ke dataBaru -> terhapus
            } else {
                dataBaru.add(baris);
            }
        }

        if (ketemu) {
            FileHelper.tulisUlang(FileHelper.FILE_SISWA, dataBaru);
            System.out.println("Siswa berhasil dihapus.");
        } else {
            System.out.println("Siswa dengan NIS " + nis + " tidak ditemukan.");
        }
    }
}
