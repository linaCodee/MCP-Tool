import {
    Box,
    Button,
    Checkbox,
    CircularProgress,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Divider,
    List,
    ListItem,
    Paper,
    Typography
} from '@mui/material';

import { useEffect, useState } from 'react';

import type {
    CreateDonationBatchRequest
} from '../../../../api/types/donation.ts';

import type {
    ResourceResponse
} from '../../../../api/types/resource.ts';

import resourceApi from "../../../../api/resourceApi.ts";

interface SubmitDonationDialogProps {
    open: boolean;
    onClose: () => void;
    onCreate: (data: CreateDonationBatchRequest) => Promise<void>;
}


const SubmitDonationDialog = ({
                                  open,
                                  onClose,
                                  onCreate
                              }: SubmitDonationDialogProps) => {

    const [resources, setResources] =
        useState<ResourceResponse[]>([]);

    const [selectedIds, setSelectedIds] =
        useState<number[]>([]);

    const [selectedResource, setSelectedResource] =
        useState<ResourceResponse | null>(null);

    const [loading, setLoading] =
        useState<boolean>(false);

    const [submitting, setSubmitting] =
        useState<boolean>(false);

    const [error, setError] =
        useState<string | null>(null);


    useEffect(() => {
        if (!open) {
            return;
        }

        const fetchResources = async () => {
            setLoading(true);
            setError(null);

            try {
                const response = await resourceApi.findAll(
                    { donated: false },
                    0,
                    200
                );

                setResources(response.data.content);

            } catch (err) {

                setError(
                    err instanceof Error
                        ? err.message
                        : 'Failed to load resources.'
                );

            } finally {
                setLoading(false);
            }
        };

        void fetchResources();

    }, [open]);


    const handleToggle = (id: number) => {

        setSelectedIds((previousIds) => {

            if (previousIds.includes(id)) {

                return previousIds.filter(
                    (resourceId) => resourceId !== id
                );
            }

            return [...previousIds, id];
        });
    };


    const handleCreate = async () => {

        if (selectedIds.length === 0) {
            return;
        }

        setSubmitting(true);

        try {

            const request: CreateDonationBatchRequest = {
                resourceIds: selectedIds
            };

            await onCreate(request);

            setSelectedIds([]);
            setSelectedResource(null);

            onClose();

        } finally {
            setSubmitting(false);
        }
    };


    const handleClose = () => {

        if (submitting) {
            return;
        }

        setSelectedIds([]);
        setSelectedResource(null);
        setError(null);

        onClose();
    };


    return (
        <Dialog
            open={open}
            onClose={handleClose}
            fullWidth
            maxWidth='md'
        >

            <DialogTitle>
                New Donation Batch
            </DialogTitle>

            <DialogContent>

                {loading && (

                    <Box
                        sx={{
                            display: 'flex',
                            justifyContent: 'center',
                            py: 4
                        }}
                    >
                        <CircularProgress />
                    </Box>

                )}


                {!loading && error && (

                    <Typography color='error'>
                        {error}
                    </Typography>

                )}


                {!loading &&
                    !error &&
                    resources.length === 0 && (

                        <Typography color='text.secondary'>
                            No resources available for this donation.
                        </Typography>

                    )}


                {!loading &&
                    !error &&
                    resources.length > 0 && (

                        <Box
                            sx={{
                                display: 'flex',
                                flexDirection:"column",
                                gap: 2,
                                mt: 1
                            }}
                        >

                            <Paper variant='outlined'>

                                <Typography
                                    variant='subtitle1'
                                    sx={{ p: 2 }}
                                >
                                    Available resources
                                </Typography>

                                <Divider />

                                <List
                                    sx={{
                                        maxHeight: 300,
                                        overflow: 'auto'
                                    }}
                                >

                                    {resources.map((resource) => (

                                        <ListItem
                                            key={resource.id}
                                            divider
                                            sx={{
                                                display: 'flex',
                                                alignItems: 'center',
                                                gap: 1
                                            }}
                                        >

                                            <Checkbox
                                                checked={
                                                    selectedIds.includes(
                                                        resource.id
                                                    )
                                                }
                                                onChange={() =>
                                                    handleToggle(resource.id)
                                                }
                                            />

                                            <Box
                                                sx={{
                                                    flex: 1,
                                                    minWidth: 0
                                                }}
                                            >

                                                <Typography
                                                    variant='body2'
                                                    fontWeight='bold'
                                                    noWrap
                                                >
                                                    {resource.title ??
                                                        'Untitled resource'}
                                                </Typography>

                                                <Typography
                                                    variant='caption'
                                                    color='text.secondary'
                                                >
                                                    Resource #{resource.id}
                                                </Typography>

                                            </Box>

                                            <Button
                                                size='small'
                                                onClick={() =>
                                                    setSelectedResource(
                                                        resource
                                                    )
                                                }
                                            >
                                                Review
                                            </Button>

                                        </ListItem>

                                    ))}

                                </List>

                            </Paper>


                            <Paper
                                variant='outlined'
                                sx={{ p: 2 }}
                            >

                                <Typography variant='subtitle1'>
                                    Resource review
                                </Typography>

                                <Divider sx={{ my: 1 }} />


                                {selectedResource === null ? (

                                    <Typography color='text.secondary'>
                                        Click Review to inspect a resource.
                                    </Typography>

                                ) : (

                                    <>

                                        <Typography
                                            variant='h6'
                                            sx={{ mb: 1 }}
                                        >
                                            {selectedResource.title ??
                                                'Untitled resource'}
                                        </Typography>


                                        <Typography
                                            variant='body2'
                                            sx={{
                                                whiteSpace: 'pre-wrap',
                                                maxHeight: 280,
                                                overflow: 'auto'
                                            }}
                                        >
                                            {selectedResource.content ??
                                                'No content available.'}
                                        </Typography>


                                        <Divider sx={{ my: 2 }} />


                                        <Typography
                                            variant='caption'
                                            color='text.secondary'
                                            sx={{
                                                display: 'block',
                                                wordBreak: 'break-all'
                                            }}
                                        >
                                            {selectedResource.sourceUrl ??
                                                'No source URL'}
                                        </Typography>


                                        <Typography
                                            variant='caption'
                                            color='text.secondary'
                                            sx={{
                                                display: 'block',
                                                mt: 1
                                            }}
                                        >
                                            Macedonian confidence:{' '}
                                            {selectedResource
                                                    .macedonianConfidence ??
                                                'N/A'}
                                        </Typography>

                                    </>

                                )}

                            </Paper>

                        </Box>

                    )}

            </DialogContent>


            <DialogActions>

                <Button
                    onClick={handleClose}
                    disabled={submitting}
                >
                    Cancel
                </Button>

                <Button
                    variant='contained'
                    onClick={handleCreate}
                    disabled={
                        submitting ||
                        selectedIds.length === 0
                    }
                >
                    {submitting
                        ? 'Creating...'
                        : `Create Batch (${selectedIds.length})`
                    }
                </Button>

            </DialogActions>

        </Dialog>
    );
};

export default SubmitDonationDialog;