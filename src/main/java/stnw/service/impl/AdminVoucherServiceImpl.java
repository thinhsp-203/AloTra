package stnw.service.impl;

import stnw.dao.VoucherDao;
import stnw.dao.impl.VoucherDaoImpl;
import stnw.model.Voucher;
import stnw.service.AdminVoucherService;

import java.util.List;

public class AdminVoucherServiceImpl implements AdminVoucherService {
    
    private final VoucherDao voucherDao = new VoucherDaoImpl();
    
    @Override
    public List<Voucher> getAllVouchers() {
        return voucherDao.findAll();
    }
    
    @Override
    public Voucher getVoucherById(int id) {
        return voucherDao.findById(id);
    }
    
    @Override
    public void saveVoucher(Voucher voucher) {
        if (voucher.getVoucher_id() == null) {
            voucher.setUsed_count(0);
            voucherDao.save(voucher);
        } else {
            voucherDao.update(voucher);
        }
    }
    
    @Override
    public void deleteVoucher(int id) {
        voucherDao.delete(id);
    }
}
