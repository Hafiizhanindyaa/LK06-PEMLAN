package src;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Transaksi {

    // Format tanggal yang digunakan di seluruh program
    static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Denda per hari keterlambatan (dalam Rupiah)
    static final long DENDA_PER_HARI = 1000;

    // Menu transaksi untuk akses
    public static void menuTransaksi(Scanner input) {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("        MENU TRANSAKSI PEMINJAMAN        ");
            System.out.println("==========================================");
            System.out.println("  1. Pinjam Buku");
            System.out.println("  2. Kembalikan Buku");
            System.out.println("  3. Lihat Semua Transaksi");
            System.out.println("  4. Cek Status Pinjaman Siswa");
            System.out.println("  0. Kembali ke Menu Utama");
            System.out.println("==========================================");
            System.out.print("Pilih menu: ");
            String pilihan = input.nextLine().trim();

            switch (pilihan) {
                case "1": pinjamBuku(input);          break;
                case "2": kembalikanBuku(input);      break;
                case "3": lihatSemuaTransaksi();      break;
                case "4": cekPinjamanSiswa(input);    break;
                case "0": return;
                default:  System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Menu untuk menampilkan fitur laporan
    public static void menuLaporan(Scanner input) {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("             MENU LAPORAN                ");
            System.out.println("==========================================");
            System.out.println("  1. Buku yang Belum Dikembalikan");
            System.out.println("  2. Peminjam yang Sudah Melewati Jatuh Tempo");
            System.out.println("  3. Riwayat Peminjaman per Siswa");
            System.out.println("  4. Statistik Buku Paling Sering Dipinjam");
            System.out.println("  0. Kembali ke Menu Utama");
            System.out.println("==========================================");
            System.out.print("Pilih menu: ");
            String pilihan = input.nextLine().trim();

            switch (pilihan) {
                case "1": laporanBelumKembali();   break;
                case "2": laporanJatuhTempo();     break;
                case "3": laporanPerSiswa(input);  break;
                case "4": laporanBukuPopuler();    break;
                case "0": return;
                default:  System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Peminjaman buku
    static void pinjamBuku(Scanner input) {
        System.out.println("\n--- PEMINJAMAN BUKU ---");

        // Langkah 1: Input kode transaksi manual oleh pegawai
        System.out.print("Kode Transaksi : ");
        String kodeTransaksi = input.nextLine().trim().toUpperCase();

        if (kodeTransaksi.isEmpty()) {
            System.out.println("Kode transaksi tidak boleh kosong.");
            return;
        }

        // Cek apakah kode transaksi sudah dipakai sebelumnya
        ArrayList<String[]> semuaTransaksi = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        for (String[] baris : semuaTransaksi) {
            if (baris.length >= 1 && baris[0].equalsIgnoreCase(kodeTransaksi)) {
                System.out.println("Kode transaksi " + kodeTransaksi + " sudah digunakan.");
                return;
            }
        }

        // Cek validitas siswa
        System.out.print("NIS Siswa      : ");
        String nis = input.nextLine().trim().toUpperCase();
        String[] siswa = FileHelper.cariSiswa(nis);

        if (siswa == null) {
            System.out.println("Siswa dengan NIS " + nis + " tidak ditemukan.");
            return;
        }

        // Cek batas pinjaman (maks 2 buku)
        int jumlahPinjaman = FileHelper.hitungPinjamanAktif(nis);
        if (jumlahPinjaman >= 2) {
            System.out.println(siswa[1] + " sudah meminjam 2 buku. Kembalikan dulu sebelum meminjam lagi.");
            return;
        }

        // Cek validitas buku
        System.out.print("Kode Buku      : ");
        String kodeBuku = input.nextLine().trim().toUpperCase();
        String[] buku = FileHelper.cariBuku(kodeBuku);

        if (buku == null) {
            System.out.println("Buku dengan kode " + kodeBuku + " tidak ditemukan.");
            return;
        }

        if (FileHelper.apakahBukuDipinjam(kodeBuku)) {
            System.out.println("Buku \"" + buku[1] + "\" sedang dipinjam oleh orang lain.");
            return;
        }

        // Input tanggal pinjam
        System.out.print("Tanggal Pinjam (dd-MM-yyyy): ");
        String inputTglPinjam = input.nextLine().trim();
        LocalDate tanggalPinjam;

        try {
            tanggalPinjam = LocalDate.parse(inputTglPinjam, FORMAT_TANGGAL);
        } catch (Exception e) {
            System.out.println("Format tanggal salah. Gunakan format dd-MM-yyyy, contoh: 19-04-2026");
            return;
        }

        // Input tanggal jatuh tempo
        System.out.print("Tanggal Jatuh Tempo (dd-MM-yyyy): ");
        String inputTglKembali = input.nextLine().trim();
        LocalDate tanggalKembali;

        try {
            tanggalKembali = LocalDate.parse(inputTglKembali, FORMAT_TANGGAL);
        } catch (Exception e) {
            System.out.println("Format tanggal salah. Gunakan format dd-MM-yyyy, contoh: 26-04-2026");
            return;
        }

        // Jatuh tempo tidak boleh sebelum tanggal pinjam
        if (!tanggalKembali.isAfter(tanggalPinjam)) {
            System.out.println("Tanggal jatuh tempo harus setelah tanggal pinjam.");
            return;
        }

        // Simpan ke file
        // Format penulisan: KodeTransaksi|NIS|KodeBuku|TglPinjam|TglKembali|Status
        FileHelper.tambahBaris(FileHelper.FILE_TRANSAKSI, new String[]{
            kodeTransaksi,
            nis,
            kodeBuku,
            tanggalPinjam.format(FORMAT_TANGGAL),
            tanggalKembali.format(FORMAT_TANGGAL),
            "0"   // 0 = belum dikembalikan
        });

        // Tampilkan struk
        System.out.println("\n===== STRUK PEMINJAMAN =====");
        System.out.println("Kode Transaksi : " + kodeTransaksi);
        System.out.println("Nama Siswa     : " + siswa[1] + " (NIS: " + nis + ")");
        System.out.println("Buku           : " + buku[1] + " [" + buku[2] + "]");
        System.out.println("Tanggal Pinjam : " + tanggalPinjam.format(FORMAT_TANGGAL));
        System.out.println("Jatuh Tempo    : " + tanggalKembali.format(FORMAT_TANGGAL));
        System.out.println("Sisa kuota     : " + (2 - jumlahPinjaman - 1) + " buku lagi");
        System.out.println("============================");
    }

    // Mengembalikan buku
    static void kembalikanBuku(Scanner input) {
        System.out.println("\n--- PENGEMBALIAN BUKU ---");
        System.out.print("Kode Transaksi: ");
        String kodeTrx = input.nextLine().trim().toUpperCase();

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        boolean ketemu = false;

        for (String[] baris : semuaData) {
            if (baris.length >= 6 && baris[0].equalsIgnoreCase(kodeTrx)) {

                // Cek jika sudah dikembalikan sebelumnya
                if (baris[5].equals("1")) {
                    System.out.println("Buku ini sudah tercatat dikembalikan sebelumnya.");
                    return;
                }

                // Input tanggal pengembalian
                System.out.print("Tanggal Pengembalian (dd-MM-yyyy): ");
                String inputTglKembali = input.nextLine().trim();
                LocalDate tanggalDikembalikan;

                try {
                    tanggalDikembalikan = LocalDate.parse(inputTglKembali, FORMAT_TANGGAL);
                } catch (Exception e) {
                    System.out.println("Format tanggal salah. Gunakan format dd-MM-yyyy.");
                    return;
                }

                // Tandai sebagai sudah dikembalikan
                baris[5] = "1";
                ketemu = true;

                // Hitung keterlambatan 
                LocalDate jatuhTempo = LocalDate.parse(baris[4], FORMAT_TANGGAL);
                long hariTerlambat   = ChronoUnit.DAYS.between(jatuhTempo, tanggalDikembalikan);
                if (hariTerlambat < 0) hariTerlambat = 0;

                String[] bukuData  = FileHelper.cariBuku(baris[2]);
                String[] siswaData = FileHelper.cariSiswa(baris[1]);
                String judulBuku   = (bukuData  != null) ? bukuData[1]  : baris[2];
                String namaSiswa   = (siswaData != null) ? siswaData[1] : baris[1];

                System.out.println("\n===== STRUK PENGEMBALIAN =====");
                System.out.println("Kode Transaksi   : " + kodeTrx);
                System.out.println("Nama Siswa       : " + namaSiswa);
                System.out.println("Buku             : " + judulBuku);
                System.out.println("Jatuh Tempo      : " + baris[4]);
                System.out.println("Tgl Dikembalikan : " + tanggalDikembalikan.format(FORMAT_TANGGAL));

                if (hariTerlambat > 0) {
                    long denda = hariTerlambat * DENDA_PER_HARI;
                    System.out.println("Status           : TERLAMBAT " + hariTerlambat + " hari");
                    System.out.println("Denda            : Rp " + denda);
                } else {
                    System.out.println("Status           : Tepat Waktu");
                    System.out.println("Denda            : Rp 0");
                }
                System.out.println("==============================");
                break;
            }
        }

        if (ketemu) {
            FileHelper.tulisUlang(FileHelper.FILE_TRANSAKSI, semuaData);
            System.out.println("Pengembalian buku berhasil dicatat.");
        } else {
            System.out.println("Kode transaksi tidak ditemukan.");
        }
    }

    // Lihat semua transaksi
    static void lihatSemuaTransaksi() {
        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);

        if (semuaData.isEmpty()) {
            System.out.println("\nBelum ada data transaksi.");
            return;
        }

        System.out.println("\n--- SEMUA TRANSAKSI (" + semuaData.size() + " data) ---");
        System.out.println("Kode Transaksi  NIS        Kode Buku   Tgl Pinjam   Jatuh Tempo  Status");
        System.out.println("--------------------------------------------------------------------------");

        for (String[] baris : semuaData) {
            if (baris.length >= 6) {
                String status = baris[5].equals("0") ? "Dipinjam" : "Dikembalikan";
                System.out.printf("%-16s%-11s%-12s%-13s%-13s%s%n",
                    baris[0], baris[1], baris[2], baris[3], baris[4], status);
            }
        }
    }

    // Cek status pinjaman siswa tertentu
    static void cekPinjamanSiswa(Scanner input) {
        System.out.println("\n--- CEK PINJAMAN SISWA ---");
        System.out.print("NIS Siswa: ");
        String nis = input.nextLine().trim().toUpperCase();

        String[] siswa = FileHelper.cariSiswa(nis);
        if (siswa == null) {
            System.out.println("Siswa tidak ditemukan.");
            return;
        }

        System.out.println("\nNama          : " + siswa[1]);
        System.out.println("Pinjaman aktif: " + FileHelper.hitungPinjamanAktif(nis) + " dari 2 buku");
        System.out.println("Detail pinjaman aktif:");

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        boolean adaPinjaman = false;

        for (String[] baris : semuaData) {
            if (baris.length >= 6 && baris[1].equalsIgnoreCase(nis) && baris[5].equals("0")) {
                String[] buku = FileHelper.cariBuku(baris[2]);
                String judul  = (buku != null) ? buku[1] : baris[2];

                // Hitung keterlambatan dari tanggal jatuh tempo vs hari ini
                LocalDate jatuhTempo   = LocalDate.parse(baris[4], FORMAT_TANGGAL);
                long hariTerlambat     = ChronoUnit.DAYS.between(jatuhTempo, LocalDate.now());
                String keterangan      = (hariTerlambat > 0)
                    ? "TERLAMBAT " + hariTerlambat + " hari!"
                    : "Tepat waktu";

                System.out.println("  - " + judul + " | Jatuh tempo: " + baris[4] + " | " + keterangan);
                adaPinjaman = true;
            }
        }

        if (!adaPinjaman) {
            System.out.println("  Tidak ada pinjaman aktif.");
        }
    }

    // Laporan buku yang belum dikembalikan
    static void laporanBelumKembali() {
        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);

        System.out.println("\n===== LAPORAN: BUKU BELUM DIKEMBALIKAN =====");
        System.out.println("No   NIS        Kode Buku   Judul Buku                    Jatuh Tempo");
        System.out.println("------------------------------------------------------------------------");

        int nomor = 1;
        for (String[] baris : semuaData) {
            // Hanya tampilkan yang status = 0 (belum dikembalikan)
            if (baris.length >= 6 && baris[5].equals("0")) {
                String[] buku = FileHelper.cariBuku(baris[2]);
                String judul  = (buku != null) ? buku[1] : "-";
                System.out.printf("%-5d%-11s%-12s%-31s%s%n",
                    nomor, baris[1], baris[2], judul, baris[4]);
                nomor++;
            }
        }

        if (nomor == 1) {
            System.out.println("  Semua buku sudah dikembalikan.");
        } else {
            System.out.println("\nTotal: " + (nomor - 1) + " buku belum dikembalikan.");
        }
    }

    // Laporan peminjam yang melewati jatuh tempo
    static void laporanJatuhTempo() {
        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        LocalDate hariIni = LocalDate.now();

        System.out.println("\n===== LAPORAN: PEMINJAM MELEWATI JATUH TEMPO =====");
        System.out.println("NIS        Nama                   Kode Buku   Jatuh Tempo  Telat(hari)  Denda");
        System.out.println("-------------------------------------------------------------------------------");

        int jumlah     = 0;
        long totalDenda = 0;

        for (String[] baris : semuaData) {
            if (baris.length >= 6 && baris[5].equals("0")) {
                LocalDate jatuhTempo = LocalDate.parse(baris[4], FORMAT_TANGGAL);
                long terlambat       = ChronoUnit.DAYS.between(jatuhTempo, hariIni);

                if (terlambat > 0) {
                    long denda   = terlambat * DENDA_PER_HARI;
                    totalDenda  += denda;

                    String[] siswa = FileHelper.cariSiswa(baris[1]);
                    String nama    = (siswa != null) ? siswa[1] : "-";

                    System.out.printf("%-11s%-23s%-12s%-13s%-13d%s%n",
                        baris[1], nama, baris[2], baris[4], terlambat, "Rp " + denda);
                    jumlah++;
                }
            }
        }

        if (jumlah == 0) {
            System.out.println("  Tidak ada peminjam yang melewati jatuh tempo.");
        } else {
            System.out.println("\nJumlah     : " + jumlah + " peminjam terlambat");
            System.out.println("Total Denda: Rp " + totalDenda);
        }
    }

    // Laporan riwayat peminjaman satu siswa tertentu
    static void laporanPerSiswa(Scanner input) {
        System.out.print("NIS Siswa: ");
        String nis = input.nextLine().trim().toUpperCase();

        String[] siswa = FileHelper.cariSiswa(nis);
        if (siswa == null) {
            System.out.println("Siswa tidak ditemukan.");
            return;
        }

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);

        System.out.println("\n===== RIWAYAT PEMINJAMAN: " + siswa[1] + " =====");
        System.out.println("Kode Transaksi   Judul Buku                    Tgl Pinjam   Jatuh Tempo  Status");
        System.out.println("-----------------------------------------------------------------------------------");

        int total        = 0;
        int sudahKembali = 0;

        for (String[] baris : semuaData) {
            if (baris.length >= 6 && baris[1].equalsIgnoreCase(nis)) {
                String[] buku = FileHelper.cariBuku(baris[2]);
                String judul  = (buku != null) ? buku[1] : "-";
                String status = baris[5].equals("0") ? "Belum Kembali" : "Sudah Kembali";

                System.out.printf("%-17s%-31s%-13s%-13s%s%n",
                    baris[0], judul, baris[3], baris[4], status);
                total++;
                if (baris[5].equals("1")) sudahKembali++;
            }
        }

        if (total == 0) {
            System.out.println("  Tidak ada riwayat peminjaman.");
        } else {
            System.out.println("\nTotal: " + total + " pinjaman | Selesai: " + sudahKembali
                + " | Aktif: " + (total - sudahKembali));
        }
    }

    // Laporan buku yang paling sering dipinjam
    static void laporanBukuPopuler() {
        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);

        if (semuaData.isEmpty()) {
            System.out.println("Belum ada data transaksi.");
            return;
        }

        // Kumpulkan semua kode buku yang unik
        ArrayList<String> daftarKode = new ArrayList<>();
        for (String[] baris : semuaData) {
            if (baris.length >= 3) {
                boolean sudahAda = false;
                for (String k : daftarKode) {
                    if (k.equalsIgnoreCase(baris[2])) {
                        sudahAda = true;
                        break;
                    }
                }
                if (!sudahAda) daftarKode.add(baris[2]);
            }
        }

        // Hitung berapa kali tiap buku dipinjam
        int[] jumlahPinjam = new int[daftarKode.size()];
        for (String[] baris : semuaData) {
            if (baris.length >= 3) {
                for (int i = 0; i < daftarKode.size(); i++) {
                    if (daftarKode.get(i).equalsIgnoreCase(baris[2])) {
                        jumlahPinjam[i]++;
                    }
                }
            }
        }

        // Urutkan dari yang terbanyak
        for (int i = 0; i < daftarKode.size() - 1; i++) {
            for (int j = 0; j < daftarKode.size() - 1 - i; j++) {
                if (jumlahPinjam[j] < jumlahPinjam[j + 1]) {
                    int tempJumlah    = jumlahPinjam[j];
                    jumlahPinjam[j]   = jumlahPinjam[j + 1];
                    jumlahPinjam[j+1] = tempJumlah;

                    String tempKode   = daftarKode.get(j);
                    daftarKode.set(j,   daftarKode.get(j + 1));
                    daftarKode.set(j+1, tempKode);
                }
            }
        }

        System.out.println("\n===== LAPORAN: BUKU PALING SERING DIPINJAM =====");
        System.out.println("Rank  Kode Buku   Judul Buku                       Total Dipinjam");
        System.out.println("--------------------------------------------------------------------");

        for (int i = 0; i < daftarKode.size(); i++) {
            String[] buku = FileHelper.cariBuku(daftarKode.get(i));
            String judul  = (buku != null) ? buku[1] : "-";
            System.out.printf("%-6d%-12s%-33s%d kali%n",
                (i + 1), daftarKode.get(i), judul, jumlahPinjam[i]);
        }
    }
}

