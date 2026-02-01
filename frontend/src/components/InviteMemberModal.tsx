import {type FormEvent, useState} from 'react';
import type {ApiError, GroupMember} from '@/types';
import {groupService} from '@/services';
import styles from './Modal.module.css';

interface Props {
  groupId: number;
  onClose: () => void;
  onInvited: (member: GroupMember) => void;
}

export function InviteMemberModal({ groupId, onClose, onInvited }: Props) {
  const [userTag, setUserTag] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const member = await groupService.inviteMember(groupId, userTag);
      onInvited(member);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Failed to invite member');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <h2>&#x1F465; Invite Member</h2>
          <button className={styles.closeBtn} onClick={onClose}>
            &times;
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="userTag">User Tag</label>
            <input
              id="userTag"
              type="text"
              className="input"
              value={userTag}
              onChange={(e) => setUserTag(e.target.value)}
              placeholder="e.g., Player#1234"
              required
            />
            <small style={{ color: 'var(--color-text-light)', marginTop: '4px', display: 'block' }}>
              Enter the user's tag in format: nickname#discriminator
            </small>
          </div>

          {error && <p className={styles.error}>{error}</p>}

          <div className={styles.actions}>
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={isLoading}>
              {isLoading ? 'Inviting...' : 'Send Invite'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
