document.addEventListener("DOMContentLoaded", (event) =>{
    document.querySelectorAll(".dropdown-item.collection-item").forEach((item) => {
        item.addEventListener("click", (e) => {
            const dataset = e.target.dataset;
            const collectionType = e.target.parentElement.parentElement.dataset.collectionType;
            if (document.getElementById("collectionType")) {
                document.getElementById("collectionType").value = collectionType;
            }
            if (document.getElementById("collectionItemId")) {
                document.getElementById("collectionItemId").value = dataset.itemId;
            }
            document.getElementById("name").value = dataset.itemName;
            document.getElementById("description").value = dataset.itemDescription;
            document.getElementById("notes").value = dataset.itemNotes;
            document.getElementById("priority").value = dataset.itemPriority;
        });
    })
});