import type { ResourceResponse } from "../../../../api/types/resource.ts";
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle
} from "@mui/material";

interface DeleteResourceDialogProps {
    resource: ResourceResponse;
    open: boolean;
    onClose: () => void;
    onDelete: (id: number) => Promise<void>;
}

const DeleteResourceDialog = ({
                                  resource,
                                  open,
                                  onClose,
                                  onDelete
                              }: DeleteResourceDialogProps) => {

    const handleSubmit = async () => {
        await onDelete(resource.id);
        onClose();
    };

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>
                Delete Resource
            </DialogTitle>

            <DialogContent>
                <DialogContentText>
                    Are you sure you want to delete{" "}
                    <strong>{resource.title}</strong>?
                    This action cannot be undone.
                </DialogContentText>
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose}>
                    Cancel
                </Button>

                <Button
                    onClick={() => void handleSubmit()}
                    color='error'
                    variant='contained'
                >
                    Delete
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default DeleteResourceDialog;