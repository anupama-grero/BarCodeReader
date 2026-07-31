const PRODUCT_API_URL = '/api/products';
const SUPPLIER_API_URL = '/api/suppliers';

const form = document.getElementById('product-form');
const formTitle = document.getElementById('form-title');
const idField = document.getElementById('product-id');
const barcodeField = document.getElementById('barcode');
const nameField = document.getElementById('name');
const descriptionField = document.getElementById('description');
const priceField = document.getElementById('price');
const quantityField = document.getElementById('quantity');
const supplierSelect = document.getElementById('supplier-select');
const cancelBtn = document.getElementById('cancel-btn');
const tableBody = document.getElementById('product-table-body');

const supplierForm = document.getElementById('supplier-form');
const supplierIdField = document.getElementById('supplier-id');
const supplierNameField = document.getElementById('supplier-name');
const supplierEmailField = document.getElementById('supplier-email');
const supplierPhoneField = document.getElementById('supplier-phone');
const supplierAddressField = document.getElementById('supplier-address');
const supplierCompanyField = document.getElementById('supplier-company');
const supplierSubmitBtn = document.getElementById('supplier-submit-btn');
const supplierClearBtn = document.getElementById('supplier-clear-btn');
const supplierMessage = document.getElementById('supplier-message');
const supplierTableBody = document.getElementById('supplier-table-body');

const scanInput = document.getElementById('scan-input');
const scanStatus = document.getElementById('scan-status');

async function loadProducts() {
  const res = await fetch(PRODUCT_API_URL);
  const products = await res.json();
  tableBody.innerHTML = '';
  products.forEach(p => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${p.barcode}</td>
      <td>${p.name}</td>
      <td>${p.description ?? ''}</td>
      <td>${p.price}</td>
      <td>${p.quantity}</td>
      <td>
        <button data-action="edit" data-id="${p.id}">Edit</button>
        <button data-action="delete" data-id="${p.id}">Delete</button>
      </td>`;
    tableBody.appendChild(tr);
  });
}

async function loadSuppliers() {
  const res = await fetch(SUPPLIER_API_URL);
  const suppliers = await res.json();
  supplierTableBody.innerHTML = '';
  supplierSelect.innerHTML = '<option value="">-- Select Supplier --</option>';

  suppliers.forEach(supplier => {
    const option = document.createElement('option');
    option.value = supplier.id;
    option.textContent = `${supplier.companyName || supplier.name}`;
    supplierSelect.appendChild(option);

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${supplier.name}</td>
      <td>${supplier.companyName || ''}</td>
      <td>${supplier.email}</td>
      <td>${supplier.phone}</td>
      <td>${supplier.address || ''}</td>
      <td>${(supplier.products || []).length}</td>
      <td>
        <button type="button" data-action="edit-supplier" data-id="${supplier.id}">Edit</button>
        <button type="button" data-action="delete-supplier" data-id="${supplier.id}">Delete</button>
      </td>`;
    supplierTableBody.appendChild(tr);
  });
}

function resetForm() {
  form.reset();
  idField.value = '';
  supplierSelect.value = '';
  formTitle.textContent = 'Add product';
  cancelBtn.hidden = true;
}

function fillForm(product) {
  idField.value = product.id;
  barcodeField.value = product.barcode;
  nameField.value = product.name;
  descriptionField.value = product.description ?? '';
  priceField.value = product.price;
  quantityField.value = product.quantity;
  if (product.supplierId) {
    supplierSelect.value = product.supplierId;
  } else if (product.supplier && product.supplier.id) {
    supplierSelect.value = product.supplier.id;
  } else {
    supplierSelect.value = '';
  }
  formTitle.textContent = `Edit product #${product.id}`;
  cancelBtn.hidden = false;
}

function clearSupplierForm() {
  supplierForm.reset();
  supplierIdField.value = '';
  supplierSubmitBtn.textContent = 'Add Supplier';
  supplierMessage.textContent = '';
  supplierMessage.className = 'message';
}

function showSupplierMessage(message, isError = false) {
  supplierMessage.textContent = message;
  supplierMessage.className = isError ? 'message error' : 'message ok';
}

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  const payload = {
    barcode: barcodeField.value.trim(),
    name: nameField.value.trim(),
    description: descriptionField.value.trim(),
    price: parseFloat(priceField.value),
    quantity: parseInt(quantityField.value, 10),
    supplier: supplierSelect.value ? { id: parseInt(supplierSelect.value, 10) } : null,
  };

  const id = idField.value;
  const res = await fetch(id ? `${PRODUCT_API_URL}/${id}` : PRODUCT_API_URL, {
    method: id ? 'PUT' : 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    alert(err.message || 'Request failed');
    return;
  }

  resetForm();
  await loadProducts();
});

cancelBtn.addEventListener('click', resetForm);

tableBody.addEventListener('click', async (e) => {
  const btn = e.target.closest('button');
  if (!btn) return;
  const id = btn.dataset.id;

  if (btn.dataset.action === 'edit') {
    const res = await fetch(`${PRODUCT_API_URL}/${id}`);
    if (res.ok) fillForm(await res.json());
  }

  if (btn.dataset.action === 'delete') {
    if (!confirm('Delete this product?')) return;
    await fetch(`${PRODUCT_API_URL}/${id}`, { method: 'DELETE' });
    await loadProducts();
  }
});

supplierForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const payload = {
    name: supplierNameField.value.trim(),
    email: supplierEmailField.value.trim(),
    phone: supplierPhoneField.value.trim(),
    address: supplierAddressField.value.trim(),
    companyName: supplierCompanyField.value.trim(),
  };

  const id = supplierIdField.value;
  const res = await fetch(id ? `${SUPPLIER_API_URL}/${id}` : SUPPLIER_API_URL, {
    method: id ? 'PUT' : 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    showSupplierMessage(data.message || 'Request failed', true);
    return;
  }

  clearSupplierForm();
  await loadSuppliers();
  showSupplierMessage('Supplier saved successfully');
});

supplierClearBtn.addEventListener('click', clearSupplierForm);

supplierTableBody.addEventListener('click', async (e) => {
  const btn = e.target.closest('button');
  if (!btn) return;
  const id = btn.dataset.id;

  if (btn.dataset.action === 'edit-supplier') {
    const res = await fetch(`${SUPPLIER_API_URL}/${id}`);
    if (!res.ok) return;
    const supplier = await res.json();
    supplierIdField.value = supplier.id;
    supplierNameField.value = supplier.name;
    supplierEmailField.value = supplier.email;
    supplierPhoneField.value = supplier.phone;
    supplierAddressField.value = supplier.address || '';
    supplierCompanyField.value = supplier.companyName || '';
    supplierSubmitBtn.textContent = 'Update Supplier';
    showSupplierMessage('Editing supplier');
  }

  if (btn.dataset.action === 'delete-supplier') {
    if (!confirm('Delete this supplier?')) return;
    const res = await fetch(`${SUPPLIER_API_URL}/${id}`, { method: 'DELETE' });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
      showSupplierMessage(data.message || 'Delete failed', true);
      return;
    }
    clearSupplierForm();
    await loadSuppliers();
    showSupplierMessage('Supplier deleted successfully');
  }
});

// USB barcode scanners behave like a keyboard: they type the code then send Enter.
scanInput.addEventListener('keydown', async (e) => {
  if (e.key !== 'Enter') return;
  e.preventDefault();

  const barcode = scanInput.value.trim();
  scanInput.value = '';
  if (!barcode) return;

  const res = await fetch(`${PRODUCT_API_URL}/barcode/${encodeURIComponent(barcode)}`);
  if (res.ok) {
    const product = await res.json();
    fillForm(product);
    scanStatus.textContent = `Found: ${product.name}`;
    scanStatus.className = 'ok';
  } else {
    resetForm();
    barcodeField.value = barcode;
    scanStatus.textContent = 'Not found — fill in details to add it';
    scanStatus.className = 'error';
  }
});

loadProducts();
loadSuppliers();
