package stnw.service;

import java.util.List;

import stnw.model.Voucher;

public interface AdminVoucherService {
    List<Voucher> getAllVouchers();
    Voucher getVoucherById(int id);
    void saveVoucher(Voucher voucher);
    void deleteVoucher(int id);
}