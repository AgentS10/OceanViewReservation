/**
 * bill.js — Loads and renders the bill for a reservation.
 */

requireAuth();
renderNavUser();

const params = new URLSearchParams(window.location.search);
const resNum = params.get('num');

if (!resNum) {
    window.location.href = '/reservations.html';
} else {
    loadBill(resNum);
}

async function loadBill(num) {
    const container = document.getElementById('bill-container');
    container.innerHTML = '<div style="text-align:center;padding:2rem"><span class="spinner"></span></div>';

    try {
        const bill = await BillAPI.get(num);

        if (!bill || !bill.reservationNumber) {
            container.innerHTML = '<div class="alert alert-danger">Bill not found for reservation: ' + num + '</div>';
            return;
        }

        const statusMap = {
            'CONFIRMED':   'badge-success',
            'CHECKED_IN':  'badge-info',
            'CHECKED_OUT': 'badge-secondary',
            'CANCELLED':   'badge-danger'
        };

        container.innerHTML = `
            <div class="bill-card">
                <div class="bill-header">
                    <h2>🌊 Ocean View Resort</h2>
                    <p>Galle, Sri Lanka &nbsp;|&nbsp; Tel: +94 91 234 5678 &nbsp;|&nbsp; info@oceanviewresort.lk</p>
                </div>
                <div class="bill-body">
                    <div style="display:flex;justify-content:space-between;margin-bottom:1.2rem;">
                        <div>
                            <div class="detail-item">
                                <label>Reservation No.</label>
                                <p><strong>${bill.reservationNumber}</strong></p>
                            </div>
                            <div class="detail-item" style="margin-top:.6rem">
                                <label>Guest Name</label>
                                <p>${bill.guestName}</p>
                            </div>
                        </div>
                        <div style="text-align:right">
                            <div class="detail-item">
                                <label>Status</label>
                                <p><span class="badge ${statusMap[bill.status] || 'badge-secondary'}">${bill.status}</span></p>
                            </div>
                            <div class="detail-item" style="margin-top:.6rem">
                                <label>Bill Date</label>
                                <p>${new Date().toLocaleDateString()}</p>
                            </div>
                        </div>
                    </div>

                    <table style="width:100%;border-collapse:collapse;margin-bottom:1rem;font-size:1rem;">
                        <thead>
                            <tr style="background:linear-gradient(135deg,var(--primary-dark),var(--primary));color:#fff;">
                                <th style="padding:.85rem 1rem;text-align:left;font-size:.9rem;text-transform:uppercase;letter-spacing:.4px;">Description</th>
                                <th style="padding:.85rem 1rem;text-align:center;font-size:.9rem;text-transform:uppercase;letter-spacing:.4px;">Details</th>
                                <th style="padding:.85rem 1rem;text-align:right;font-size:.9rem;text-transform:uppercase;letter-spacing:.4px;">Amount (LKR)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td style="padding:.85rem 1rem;border-bottom:1px solid #dee2e6;font-weight:600;color:#202124;">${bill.roomTypeName} Room</td>
                                <td style="padding:.85rem 1rem;text-align:center;border-bottom:1px solid #dee2e6;color:#202124;">
                                    ${bill.checkInDate} → ${bill.checkOutDate}<br>
                                    <span style="font-size:.88rem;color:#5f6368;">${bill.numberOfNights} night(s) × LKR ${Number(bill.pricePerNight).toLocaleString()}</span>
                                </td>
                                <td style="padding:.85rem 1rem;text-align:right;border-bottom:1px solid #dee2e6;font-weight:700;color:#202124;font-size:1.05rem;">
                                    ${Number(bill.subtotal).toLocaleString('en-LK', {minimumFractionDigits:2})}
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div class="bill-row" style="color:#202124;font-weight:500;">
                        <span>Subtotal</span>
                        <span style="font-weight:600;">LKR ${Number(bill.subtotal).toLocaleString('en-LK', {minimumFractionDigits:2})}</span>
                    </div>
                    <div class="bill-row" style="color:#202124;font-weight:500;">
                        <span>Tax (${(bill.taxRate * 100).toFixed(0)}%)</span>
                        <span style="font-weight:600;">LKR ${Number(bill.taxAmount).toLocaleString('en-LK', {minimumFractionDigits:2})}</span>
                    </div>
                    <div class="bill-row bill-total" style="font-size:1.15rem;padding:1rem 0;border-top:2px solid var(--primary);margin-top:.5rem;">
                        <span>TOTAL AMOUNT DUE</span>
                        <span>LKR ${Number(bill.totalAmount).toLocaleString('en-LK', {minimumFractionDigits:2})}</span>
                    </div>

                    <p style="margin-top:1.5rem;font-size:.82rem;color:#6c757d;text-align:center;">
                        Thank you for choosing Ocean View Resort. We hope you enjoyed your stay!
                    </p>
                </div>
            </div>
            <div class="action-bar" style="justify-content:flex-end;margin-top:1rem;">
                <button class="btn btn-secondary" onclick="window.history.back()">← Back</button>
                <button class="btn btn-primary" onclick="window.print()">🖨 Print Bill</button>
            </div>
        `;
    } catch (err) {
        container.innerHTML = '<div class="alert alert-danger">Error loading bill. Please try again.</div>';
    }
}
