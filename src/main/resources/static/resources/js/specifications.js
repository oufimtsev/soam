const TREE_ICON_MAP = {
    'specification': 'fa fa-file-text-o',
    'specificationObjective': 'fa fa-bullseye',
    'stakeholder': 'fa fa-user',
    'stakeholderObjective': 'fa fa-check-square-o'
};
const DEFAULT_MESSAGE = 'Please select an item in Entity Explorer';

function createTreeEntity(item) {
    const treeEntity = {
        'text': item.name,
        'icon': TREE_ICON_MAP[item.type],
        'data': item
    };
    return treeEntity;
}

function jsTreeDataLoader(obj, callback) {
    if (obj.id === '#') {
        callback([{
            'text': 'Specifications',
            'state': {
                'opened': true
            },
            'data': {
                'type': 'specifications'
            },
            'children': true
        }]);
    } else {
        switch (obj.data.type) {
            case 'specifications':
                fetch('/tree/specification')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.children = [
                                {
                                    'text': 'Objectives',
                                    'data': {
                                        'type': 'specificationObjectives',
                                        'specificationId': item.id
                                    },
                                    'children': true
                                },
                                {
                                    'text': 'Stakeholders',
                                    'data': {
                                        'type': 'stakeholders',
                                        'specificationId': item.id
                                    },
                                    'children': true
                                }
                            ];
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'specificationObjectives':
                fetch('/tree/specification/' + obj.data.specificationId + '/specificationObjective')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(createTreeEntity);
                        callback(children);
                    });
                break;
            case 'stakeholders':
                fetch('/tree/specification/' + obj.data.specificationId + '/stakeholder')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.children = true;
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'stakeholder':
                fetch('/tree/stakeholder/' + obj.data.id + '/stakeholderObjective')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(createTreeEntity);
                        callback(children);
                    });
                break;
        }
    }
}

function deleteEntity(entityName, url) {
    if (confirm('Are you sure you want to delete this ' + entityName + '?')) {
        $('#tree').jstree().deselect_all();
        fetch(url, {
            'method': 'POST'
        })
            .then(response => response.text())
            .then(text => {
                $('#main').html(text);
                $('#tree').jstree().refresh();
            });
    }
}

function loadMainPanel(url) {
    fetch(url)
        .then(response => response.text())
        .then(text => $('#main').html(text));
}

function createUpdateAction(node) {
    let url = '/' + node.data.type + '2/' + node.data.id + '/edit';
    for (let i = 0; i < node.parents.length; i ++) {
        const parentData = $('#tree').jstree().get_node(node.parents[i]).data;
        if (parentData && parentData.id) {
            url = '/' + parentData.type + '/' + parentData.id + url;
        }
    }
    return {
       'label': 'Update',
       'action': obj => {
           loadMainPanel(url);
       }
   };
}

$(document).ready(function () {
    loadMainPanel('/specifications2/default');

    $('#tree').jstree({
        'core': {
            'data': jsTreeDataLoader
        },
        'plugins': ['contextmenu'],
        'contextmenu': {
            'select_node': false,
            'items': (node, callback) => {
                switch (node.data.type) {
                    case 'specifications':
                        callback({
                            'createComplete': {
                                'label': 'Create Complete',
                                'action': obj => {
                                    $('#tree').jstree().deselect_all();
                                    loadMainPanel('/specification2/new?collectionType=templateDeepCopy');
                                }
                            },
                            'copyComplete': {
                                'label': 'Copy Complete',
                                'action': obj => {
                                    $('#tree').jstree().deselect_all();
                                    loadMainPanel('/specification2/new?collectionType=srcSpecification');
                                }
                            },
                            'copyTemplate': {
                                'label': 'Copy Template',
                                'action': obj => {
                                    $('#tree').jstree().deselect_all();
                                    loadMainPanel('/specification2/new?collectionType=copyTemplate');
                                }
                            },
                            'enter': {
                                'label': 'Enter',
                                'action': obj => {
                                    $('#tree').jstree().deselect_all();
                                    loadMainPanel('/specification2/new');
                                }
                            }
                        });
                        break;
                    case 'specificationObjectives':
                        callback({
                            'copyTemplate': {
                                'label': 'Copy Template',
                                'action': obj => {
                                    $('#tree').jstree().deselect_all();
                                    loadMainPanel('/specification/' + node.data.specificationId + '/specificationObjective2/new?collectionType=copyTemplate');
                                }
                            },
                            'enter': {
                                'label': 'Enter',
                                'action': obj => {
                                    $('#tree').jstree().deselect_all();
                                    loadMainPanel('/specification/' + node.data.specificationId + '/specificationObjective2/new');
                                }
                            }
                        });
                        break;
                    case 'stakeholders':
                        callback({
                            'copyTemplate': {
                                'label': 'Copy Template',
                                'action': obj => {
                                    $('#tree').jstree().deselect_all();
                                    loadMainPanel('/specification/' + node.data.specificationId + '/stakeholder2/new?collectionType=copyTemplate');
                                }
                            },
                            'enter': {
                                'label': 'Enter',
                                'action': obj => {
                                    $('#tree').jstree().deselect_all();
                                    loadMainPanel('/specification/' + node.data.specificationId + '/stakeholder2/new');
                                }
                            }
                        });
                        break;
                    case 'specification':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': {
                                'label': 'Delete',
                                'action': obj => {
                                    deleteEntity('Specification', '/specification2/' + node.data.id + '/delete');
                                }
                            }
                        });
                        break;
                    case 'specificationObjective':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': {
                                'label': 'Delete',
                                'action': obj => {
                                    deleteEntity('Specification Objective', '/specification/' + node.data.specificationId + '/specificationObjective2/' + node.data.id + '/delete');
                                }
                            }
                        });
                        break;
                    case 'stakeholder':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': {
                                'label': 'Delete',
                                'action': obj => {
                                    deleteEntity('Stakeholder', '/specification/' + node.data.specificationId + '/stakeholder2/' + node.data.id + '/delete');
                                }
                            },
                            'addObjective': {
                                'label': 'Add Objective',
                                'action': obj => {
                                    $('#tree').jstree().deselect_all();
                                    loadMainPanel('/specification/' + node.data.specificationId + '/stakeholder/' + node.data.id + '/stakeholderObjective2/new');
                                }
                            }
                        });
                        break;
                    case 'stakeholderObjective':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': {
                                'label': 'Delete',
                                'action': obj => {
                                    deleteEntity('Stakeholder Objective', '/specification/' + node.data.specificationId + '/stakeholder/' + node.data.stakeholderId + '/stakeholderObjective2/' + node.data.id + '/delete');
                                }
                            }
                        });
                        break;
                }
            }
        }
    });

//    $('#tree').on('activate_node.jstree', (e, data) => {
//        console.log("activate_node: data: " + data);
//        let url;
//        if (data.node.data.id) {
//            url = '/' + data.node.data.type + '2/' + data.node.data.id + '/edit';
//            for (let i = 0; i < data.node.parents.length; i ++) {
//                const parentData = $('#tree').jstree().get_node(data.node.parents[i]).data;
//                if (parentData && parentData.id) {
//                    url = '/' + parentData.type + '/' + parentData.id + url;
//                }
//            }
//        } else {
//            url = '/specifications2/default';
//        }
//        loadMainPanel(url);
//    });
})

function soamFormSubmit(form) {
    const action = form.action;
    fetch(form.action, {
        method: form.method,
        body: new FormData(form)
    })
        .then(response => response.text())
        .then(text => {
            $('#main').html(text);
            $('#tree').jstree().refresh();
        });
}

function soamFormCancel() {
    loadMainPanel('/specifications2/default');
}