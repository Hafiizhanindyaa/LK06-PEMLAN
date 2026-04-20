package src;

import java.util.ArrayList;
import java.util.Scanner;
public class Pegawai {
    public static void menuPegawai(Scanner input) {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("          MENU DATA PEGAWAI              ");
            System.out.println("==========================================");
            System.out.println("  1. Lihat Semua Pegawai");
            System.out.println("  2. Tambah Pegawai Baru");
            System.out.println("  3. Edit Data Pegawai");
            System.out.println("  4. Hapus Pegawai");
            System.out.println("  0. Kembali ke Menu Utama");
            System.out.println("==========================================");
            System.out.print("Pilih menu: ");
            String pilihan = input.nextLine().trim();

            switch (pilihan) {
                case "1": lihatSemuaPegawai();  break;
                case "2": tambahPegawai(input); break;
                case "3": editPegawai(input);   break;
                case "4": hapusPegawai(input);  break;
                case "0": return;
                default:  System.out.println("Pilihan tidak valid.");
            }
        }
    }
    static void lihatSemuaPegawai() {
        ArrayList<String[]> dataPegawai = FileHelper.bacaSemua(FileHelper.FILE_PEGAWAI);

        if (dataPegawai.isEmpty()) {
            System.out.println("\nBelum ada data pegawai.");
            return;
        }

        System.out.println("\n--- DAFTAR PEGAWAI (" + dataPegawai.size() + " orang) ---");
        System.out.println("No   NIP          Nama                      Tgl Lahir");
        System.out.println("------------------------------------------------------------");

        int nomor = 1;
        for (String[] baris : dataPegawai) {
            if (baris.length >= 3) {
                // Password (kolom ke-4) sengaja tidak ditampilkan
                System.out.printf("%-5d%-13s%-27s%s%n", nomor, baris[0], baris[1], baris[2]);
                nomor++;
            }
        }
    }
    static void tambahPegawai(Scanner input) {
        System.out.println("\n--- TAMBAH PEGAWAI BARU ---");

        System.out.print("NIP          : ");
        String nip = input.nextLine().trim().toUpperCase();

        if (nip.isEmpty()) {
            System.out.println("NIP tidak boleh kosong.");
            return;
        }

        if (FileHelper.cariPegawai(nip) != null) {
            System.out.println("NIP " + nip + " sudah terdaftar.");
            return;
        }

        System.out.print("Nama         : ");
        String nama = input.nextLine().trim();

        if (nama.isEmpty()) {
            System.out.println("Nama tidak boleh kosong.");
            return;
        }

        System.out.print("Tgl Lahir    : ");
        String tglLahir = input.nextLine().trim();

        System.out.print("Password     : ");
        String password = input.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Password tidak boleh kosong.");
            return;
        }

        // Simpan ke file: NIP|Nama|TanggalLahir|Password
        FileHelper.tambahBaris(FileHelper.FILE_PEGAWAI, new String[]{nip, nama, tglLahir, password});
        System.out.println("Pegawai " + nama + " berhasil ditambahkan.");
    }
    static void editPegawai(Scanner input) {
        System.out.println("\n--- EDIT DATA PEGAWAI ---");
        System.out.print("Masukkan NIP yang ingin diedit: ");
        String nip = input.nextLine().trim().toUpperCase();

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_PEGAWAI);
        boolean ketemu = false;

        for (String[] baris : semuaData) {
            if (baris.length >= 4 && baris[0].equalsIgnoreCase(nip)) {
                System.out.println("Data lama -> Nama: " + baris[1] + " | Tgl Lahir: " + baris[2]);
                System.out.println("(Kosongkan dan tekan Enter jika tidak ingin mengubah)");

                System.out.print("Nama baru        : ");
                String namaBaru = input.nextLine().trim();

                System.out.print("Tgl Lahir baru   : ");
                String tglBaru = input.nextLine().trim();

                System.out.print("Password baru    : ");
                String passBaru = input.nextLine().trim();

                if (!namaBaru.isEmpty()) baris[1] = namaBaru;
                if (!tglBaru.isEmpty())  baris[2] = tglBaru;
                if (!passBaru.isEmpty()) baris[3] = passBaru;

                ketemu = true;
                break;
            }
        }

        if (ketemu) {
            FileHelper.tulisUlang(FileHelper.FILE_PEGAWAI, semuaData);
            System.out.println("Data pegawai berhasil diperbarui.");
        } else {
            System.out.println("Pegawai dengan NIP " + nip + " tidak ditemukan.");
        }
    }
    static void hapusPegawai(Scanner input) {
        System.out.println("\n--- HAPUS PEGAWAI ---");
        System.out.print("Masukkan NIP yang ingin dihapus: ");
        String nip = input.nextLine().trim().toUpperCase();

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_PEGAWAI);

        // Harus selalu ada minimal 1 pegawai agar program bisa dijalankan
        if (semuaData.size() <= 1) {
            System.out.println("Tidak bisa dihapus. Minimal harus ada 1 pegawai aktif.");
            return;
        }

        ArrayList<String[]> dataBaru = new ArrayList<>();
        boolean ketemu = false;

        for (String[] baris : semuaData) {
            if (baris.length >= 1 && baris[0].equalsIgnoreCase(nip)) {
                ketemu = true;
                // Baris ini dilewati -> terhapus dari file
            } else {
                dataBaru.add(baris);
            }
        }

        if (ketemu) {
            FileHelper.tulisUlang(FileHelper.FILE_PEGAWAI, dataBaru);
            System.out.println("Pegawai berhasil dihapus.");
        } else {
            System.out.println("Pegawai dengan NIP " + nip + " tidak ditemukan.");
        }
    }
}
