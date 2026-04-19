package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHelper {

    // Folder penyimpanan data dipisah dari folder kode
    // Semua file .txt akan disimpan di dalam folder "data/"
    static final String FOLDER_DATA = "data";

    // Nama file (tanpa path) path lengkapnya dibentuk lewat pathFile()
    static final String FILE_SISWA     = "siswa.txt";
    static final String FILE_BUKU      = "buku.txt";
    static final String FILE_PEGAWAI   = "pegawai.txt";
    static final String FILE_TRANSAKSI = "transaksi.txt";

    // Pemisah antar kolom dalam satu baris
    static final String PEMISAH = "|";

    // Method pembantu: membentuk path lengkap ke file data
    // File.separator otomatis menyesuaikan sistem operasi yang digunakan
    private static String pathFile(String namaFile) {
        return FOLDER_DATA + File.separator + namaFile;
    }

    // Method untuk membuat folder "data/" jika belum ada
    // Dipanggil di setupAwal() agar folder selalu siap sebelum file ditulis
    private static void buatFolderDataJikaBelumAda() {
        File folder = new File(FOLDER_DATA);
        if (!folder.exists()) {
            folder.mkdirs(); 
            System.out.println("[INFO] Folder \"" + FOLDER_DATA + "\" berhasil dibuat.");
        }
    }

    
    public static ArrayList<String[]> bacaSemua(String namaFile) {
        ArrayList<String[]> hasilBaca = new ArrayList<>();

        // Menggunakan pathFile() agar membaca dari folder data/
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathFile(namaFile)));
            String baris;

            while ((baris = reader.readLine()) != null) {
                baris = baris.trim();
                if (!baris.isEmpty()) {
                    hasilBaca.add(baris.split("\\|", -1));
                }
            }

            reader.close();

        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            System.out.println("[ERROR] Gagal membaca " + namaFile + ": " + e.getMessage());
        }

        return hasilBaca;
    }

    // Menambahkan satu baris baru ke akhir file (mode append/tambah).
    // Kolom-kolom digabung dengan "|" sebelum ditulis.
    public static void tambahBaris(String namaFile, String[] kolom) {
        // Menggunakan pathFile() agar menulis ke folder data/
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile(namaFile), true));
            writer.write(gabungKolom(kolom));
            writer.newLine();
            writer.close();

        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan ke " + namaFile + ": " + e.getMessage());
        }
    }

    
    // Menulis ulang seluruh isi file dari nol.
    // Digunakan saat mengedit atau menghapus data.
    public static void tulisUlang(String namaFile, ArrayList<String[]> semuaData) {
        // Menggunakan pathFile() agar menulis ke folder data/
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile(namaFile), false));

            for (String[] baris : semuaData) {
                writer.write(gabungKolom(baris));
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan ke " + namaFile + ": " + e.getMessage());
        }
    }

    //  Menggabungkan array String menjadi satu String dengan pemisah "|"
    private static String gabungKolom(String[] kolom) {
        String hasil = "";
        for (int i = 0; i < kolom.length; i++) {
            if (i == 0) {
                hasil = kolom[i];
            } else {
                hasil = hasil + PEMISAH + kolom[i];
            }
        }
        return hasil;
    }

    // Mencari pegawai berdasarkan NIP. Kembalikan array kolomnya, atau null jika tidak ada.
    public static String[] cariPegawai(String nip) {
        ArrayList<String[]> semuaPegawai = bacaSemua(FILE_PEGAWAI);
        for (String[] baris : semuaPegawai) {
            if (baris.length >= 4 && baris[0].equalsIgnoreCase(nip)) {
                return baris;
            }
        }
        return null;
    }

    // Mencari siswa berdasarkan NIS. Kembalikan array kolomnya, atau null jika tidak ada.
    public static String[] cariSiswa(String nis) {
        ArrayList<String[]> semuaSiswa = bacaSemua(FILE_SISWA);
        for (String[] baris : semuaSiswa) {
            if (baris.length >= 3 && baris[0].equalsIgnoreCase(nis)) {
                return baris;
            }
        }
        return null;
    }

    // Mencari buku berdasarkan kode buku. Kembalikan array kolomnya, atau null jika tidak ada.
    public static String[] cariBuku(String kodeBuku) {
        ArrayList<String[]> semuaBuku = bacaSemua(FILE_BUKU);
        for (String[] baris : semuaBuku) {
            if (baris.length >= 3 && baris[0].equalsIgnoreCase(kodeBuku)) {
                return baris;
            }
        }
        return null;
    }

    //  Menghitung berapa buku yang sedang dipinjam oleh satu siswa (status = 0).
    //  Dipakai untuk mengecek batas maksimal 2 buku.
    public static int hitungPinjamanAktif(String nis) {
        ArrayList<String[]> semuaTransaksi = bacaSemua(FILE_TRANSAKSI);
        int jumlah = 0;
        for (String[] baris : semuaTransaksi) {
            if (baris.length >= 6 && baris[1].equalsIgnoreCase(nis) && baris[5].equals("0")) {
                jumlah++;
            }
        }
        return jumlah;
    }

    //  Mengecek apakah sebuah buku sedang dipinjam (status = 0).
    //  Dipakai agar satu buku tidak bisa dipinjam oleh dua orang sekaligus.
    public static boolean apakahBukuDipinjam(String kodeBuku) {
        ArrayList<String[]> semuaTransaksi = bacaSemua(FILE_TRANSAKSI);
        for (String[] baris : semuaTransaksi) {
            if (baris.length >= 6 && baris[2].equalsIgnoreCase(kodeBuku) && baris[5].equals("0")) {
                return true;
            }
        }
        return false;
    }

    // Setup awal: buat folder data/ dan akun admin default jika belum ada
    public static void setupAwal() {
        // Folder data/ dibuat lebih dulu sebelum file apapun ditulis
        buatFolderDataJikaBelumAda();

        if (bacaSemua(FILE_PEGAWAI).isEmpty()) {
            tambahBaris(FILE_PEGAWAI, new String[]{"P001", "Admin Perpus", "01-01-1990", "admin123"});
            System.out.println("============================================");
            System.out.println("  SETUP AWAL: Akun admin default dibuat.");
            System.out.println("  NIP      : P001");
            System.out.println("  Password : admin123");
            System.out.println("  Harap ganti password setelah login!");
            System.out.println("============================================");
            System.out.println();
        }
    }
}
