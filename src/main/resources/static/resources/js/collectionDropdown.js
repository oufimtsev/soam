function initCollectionDropdown() {
    document.querySelectorAll(".dropdown-item.collection-item").forEach((item) => {
        item.addEventListener("click", (e) => {
            const dataset = e.target.dataset;
            const collectionDataset = e.target.parentElement.parentElement.dataset;
            const collectionType = collectionDataset.collectionType;
            if (document.getElementById("collectionType")) {
                document.getElementById("collectionType").value = collectionType;
            }
            if (document.getElementById("collectionItemId")) {
                document.getElementById("collectionItemId").value = dataset.itemId;
            }
            document.getElementById(collectionDataset.nameField).value = dataset.itemName;
            document.getElementById(collectionDataset.descriptionField).value = dataset.itemDescription;
            document.getElementById(collectionDataset.notesField).value = dataset.itemNotes ? dataset.itemNotes : '';
            document.getElementById(collectionDataset.priorityField).value = dataset.itemPriority;
        });
    });
}

document.addEventListener("DOMContentLoaded", (event) => {
    initCollectionDropdown();
});