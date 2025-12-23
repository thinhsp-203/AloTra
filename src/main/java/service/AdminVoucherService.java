package service;

import java.util.List;

import model.Voucher;

public interface AdminVoucherService {
    List<Voucher> getAllVouchers();
    Voucher getVoucherById(int id);
    void saveVoucher(Voucher voucher);
    void deleteVoucher(int id);
}