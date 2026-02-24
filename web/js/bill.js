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

                    <table style="width:100%;border-collapse:collapse;margin-bottom:1rem;">
                        <thead>
                            <tr style="background:#f0f6fc;">
                                <th style="padding:.6rem;text-align:left;border-bottom:2px solid #dee2e6;">Description</th>
                                <th style="padding:.6rem;text-align:right;border-bottom:2px solid #dee2e6;">Details</th>
                                <th style="padding:.6rem;text-align:right;border-bottom:2px solid #dee2e6;">Amount (LKR)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td style="padding:.6rem;border-bottom:1px solid #dee2e6;">${bill.roomTypeName} Room</td>
                                <td style="padding:.6rem;text-align:right;border-bottom:1px solid #dee2e6;">
                                    ${bill.checkInDate} → ${bill.checkOutDate}<br>
                                    <small>${bill.numberOfNights} night(s) × LKR ${Number(bill.pricePerNight).toLocaleString()}</small>
                                </td>
                                <td style="padding:.6rem;text-align:right;border-bottom:1px solid #dee2e6;">
                                    ${Number(bill.subtotal).toLocaleString('en-LK', {minimumFractionDigits:2})}
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div class="bill-row">
                        <span>Subtotal</span>
                        <span>LKR ${Number(bill.subtotal).toLocaleString('en-LK', {minimumFractionDigits:2})}</span>
                    </div>
                    <div class="bill-row">
                        <span>Tax (${(bill.taxRate * 100).toFixed(0)}%)</span>
                        <span>LKR ${Number(bill.taxAmount).toLocaleString('en-LK', {minimumFractionDigits:2})}</span>
                    </div>
                    <div class="bill-row bill-total">
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
