function initCollectionDropdown() {
    const checkUpdateField = (fieldId, value) => {
        if (document.getElementById(fieldId)) {
            document.getElementById(fieldId).value = value ? value : '';
        }
    };

    document.querySelectorAll(".dropdown-item.collection-item").forEach((item) => {
        item.addEventListener("click", (e) => {
            const dataset = e.target.dataset;
            const collectionDataset = e.target.parentElement.parentElement.dataset;
            const collectionType = collectionDataset.collectionType;

            checkUpdateField("collectionType", collectionType);
            checkUpdateField(collectionDataset.idField, dataset.itemId);
            checkUpdateField(collectionDataset.nameField, dataset.itemName);
            checkUpdateField(collectionDataset.descriptionField, dataset.itemDescription);
            checkUpdateField(collectionDataset.notesField, dataset.itemNotes);
            checkUpdateField(collectionDataset.itemPriority, dataset.itemPriority);
        });
    });
}

document.addEventListener("DOMContentLoaded", (event) => {
    initCollectionDropdown();
});