import {AuctionDetailItem, AuctionItem, AuctionPurchaseRequest, AuctionsRequest} from "./type";

async function requestAuctionList(
    baseUrl: string,
    data: AuctionsRequest,
    onSuccess: (auctions: AuctionItem[]) => void,
    onFailure: () => void
) {
    try {
        const response = await fetch(`${baseUrl}/auctions?size=${data.size}&offset=${data.offset}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (response.ok) {
            const auctions: AuctionItem[] = await response.json();
            onSuccess(auctions);
        } else {
            onFailure();
        }
    } catch (error) {
        console.error('Failed to fetch auctions.', error);
        onFailure();
    }
}

async function requestAuctionDetail(
    baseUrl: string,
    auctionId: number,
    onSuccess: (auctionDetail: AuctionDetailItem) => void,
    onFailure: (message: string) => void
) {
    try {
        const response = await fetch(`${baseUrl}/auctions/${auctionId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
        });

        if (response.ok) {
            const auctionDetail: AuctionDetailItem = await response.json();
            onSuccess(auctionDetail);
        } else {
            const errorMessage = await response.text();
            onFailure(errorMessage);
        }
    } catch (error) {
        console.error('Failed to fetch auction detail.', error);
        onFailure("REQUEST AUCTION DETAIL.");
    }
}

async function requestAuctionBid(
    baseUrl: string,
    auctionId: number,
    request: AuctionPurchaseRequest,
    onSuccess: () => void,
    onFailure: (message: string) => void
) {
    try {
        const response = await fetch(`${baseUrl}/auctions/${auctionId}/purchase`, {
            method: 'POST',
            mode: 'cors',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                price: request.price,
                quantity: request.quantity,
            }),
        });

        if (response.ok) {
            onSuccess();
        } else {
            const errorMessage = await response.text();
            onFailure(errorMessage);
        }

    } catch (error) {
        console.error('Failed to bid auction.', error);
        onFailure("BID REQUEST FAILED");
    }
}

export {
    requestAuctionList,
    requestAuctionDetail,
    requestAuctionBid,
};
